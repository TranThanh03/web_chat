package com.example.chat.service;

import com.example.chat.dto.request.AuthenticationRequest;
import com.example.chat.dto.request.ExchangeTokenRequest;
import com.example.chat.dto.response.AuthenticationResponse;
import com.example.chat.entity.InvalidatedToken;
import com.example.chat.entity.User;
import com.example.chat.enums.AccountStatus;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.InvalidatedTokenRepository;
import com.example.chat.repository.httpclient.OutboundIdentityClient;
import com.example.chat.repository.httpclient.OutboundUserClient;
import com.example.chat.util.DomainUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserService userService;
    InvalidatedTokenRepository invalidatedTokenRepository;
    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${base.url}")
    protected String BASE_URL;

    @NonFinal
    @Value("${google.clientId}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${google.clientSecret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${google.redirectUrl}")
    protected String REDIRECT_URI;

    @NonFinal
    @Value("${google.grantType}")
    protected String GRANT_TYPE;

    public String authenticate(AuthenticationRequest request) {
        User user;

        if (request.getUsername().matches("\\d+")) {
            user = userService.getUserByPhone(request.getUsername());
        } else {
            user = userService.getUserByEmail(request.getUsername());
        }

        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXITED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!authenticated) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        } else if (user.getAccountStatus().equals(AccountStatus.BANNED.name())) {
            throw new AppException(ErrorCode.ACCOUNT_BANNED);
        }

        return generateToken(request.getUsername(), user);
    }

    private String generateToken(String username, User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer(DomainUtil.extractDomain(BASE_URL))
                .issueTime(new Date())
                .expirationTime(Date.from(
                        Instant.now().plusSeconds(3600)
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("id", user.getId())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse outboundAuthenticate(String code){
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        return AuthenticationResponse.builder()
                .token(response.getAccessToken())
                .build();
    }

    public Boolean introspect(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (JOSEException | ParseException e) {
            return false;
        }
    }

    public void logout(String token)
            throws ParseException, JOSEException {
        var signToken = verifyToken(token);

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime.toInstant())
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    private SignedJWT verifyToken(String token)
            throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        String id = signedJWT.getJWTClaimsSet().getClaim("id").toString();
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified= signedJWT.verify(verifier);

        if (!(verified && expityTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        } else if (userService.isCustomerBanned(id)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public String getIdByToken(String token) {
        try {
            var signToken = verifyToken(token);

            return signToken.getJWTClaimsSet().getClaim("id").toString();
        } catch (ParseException | JOSEException e) {
            return null;
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(stringJoiner::add);
        }

        return stringJoiner.toString();
    }
}