package com.example.chat.service;

import com.example.chat.dto.request.account.OAuthAccountRequest;
import com.example.chat.dto.request.auth.AuthenticationRequest;
import com.example.chat.dto.request.auth.ExchangeTokenRequest;
import com.example.chat.dto.request.auth.IntrospectRequest;
import com.example.chat.dto.request.auth.OAuthAuthenticateRequest;
import com.example.chat.dto.response.account.OAuthAccountResponse;
import com.example.chat.dto.response.auth.ExchangeTokenResponse;
import com.example.chat.dto.response.auth.IntrospectResponse;
import com.example.chat.dto.response.auth.TokenAccountIdResponse;
import com.example.chat.dto.response.user.OAuthUserResponse;
import com.example.chat.entity.Account;
import com.example.chat.enums.AccountStatus;
import com.example.chat.enums.ProviderType;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.httpclient.OAuthIdentityClient;
import com.example.chat.repository.httpclient.OAuthUserClient;
import com.example.chat.util.DomainUtils;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    AccountService accountService;
    UserService userService;
    OAuthIdentityClient OAuthIdentityClient;
    OAuthUserClient OAuthUserClient;
    PasswordEncoder passwordEncoder;
    RedisService redisService;

    @NonFinal
    @Value("${base.fe-url}")
    String BASE_FE_URL;

    @NonFinal
    @Value("${jwt.signer-key}")
    String JWT_SIGNER_KEY;

    @NonFinal
    @Value("${jwt.access-token-expiration}")
    long JWT_ACCESS_TOKEN_EXPIRATION;

    @NonFinal
    @Value("${google.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${google.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${google.redirect-url}")
    protected String REDIRECT_URI;

    @NonFinal
    @Value("${google.grant-type}")
    protected String GRANT_TYPE;

    public TokenAccountIdResponse authenticate(AuthenticationRequest request) {
        Account account = accountService.getByEmail(request.getUsername());

        if (account == null || !account.getProvider().equals(ProviderType.LOCAL.name())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if(!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (account.getStatus().equals(AccountStatus.INACTIVATE.name())) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVATE);
        }

        if (account.getStatus().equals(AccountStatus.BANNED.name())) {
            throw new AppException(ErrorCode.ACCOUNT_BANNED);
        }

        String userId = userService.getIdByAccountId(account.getId());

        return TokenAccountIdResponse.builder()
                .token(generateToken(account, userId))
                .accountId(account.getId())
                .build();
    }

    public TokenAccountIdResponse oauthAuthenticate(OAuthAuthenticateRequest request){
        ExchangeTokenResponse response = OAuthIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(request.getCode())
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        OAuthUserResponse userInfo = OAuthUserClient.getUserInfo("json", response.getAccessToken());
        Account account = accountService.getByEmail(userInfo.getEmail());
        String userId;

        if (account == null) {
            OAuthAccountResponse accountResponse = accountService.createOAuth(
                    OAuthAccountRequest.builder()
                            .firstName(userInfo.getGivenName())
                            .lastName(userInfo.getFamilyName())
                            .email(userInfo.getEmail())
                            .avatar(userInfo.getPicture())
                            .build()
            );
            account = accountResponse.getAccount();
            userId = accountResponse.getUserId();
        } else {
            userId = userService.getIdByAccountId(account.getId());
        }

        return TokenAccountIdResponse.builder()
                .token(generateToken(account, userId))
                .accountId(account.getId())
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;

        try {
            var signToken = verifyToken(request.getToken());

            if (Objects.isNull(signToken)) {
                isValid = false;
            }
        } catch (JOSEException | ParseException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public void invalidToken(String token) throws ParseException, JOSEException {
        var signToken = verifyToken(token);
        String jti = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
        long ttl = (expiryTime.getTime() - System.currentTimeMillis()) / 1000;

        if (ttl > 0) {
            redisService.setInvalidToken(jti, ttl);
        }
    }

    public String generateToken(Account account, String userId) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(account.getId())
                .issuer(DomainUtils.extractDomain(BASE_FE_URL))
                .audience("chat-api")
                .issueTime(new Date())
                .expirationTime(Date.from(
                        Instant.now().plusSeconds(JWT_ACCESS_TOKEN_EXPIRATION)
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("uid", userId)
                .claim("roles", buildScope(account))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(JWT_SIGNER_KEY.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(JWT_SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified= signedJWT.verify(verifier);

        if (!(verified && expityTime.after(new Date()))) {
            return null;
        }

        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        String accountId = signedJWT.getJWTClaimsSet().getSubject();

        if (redisService.isInvalidToken(jti)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (accountService.hasStatus(accountId, AccountStatus.INACTIVATE)) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVATE);
        }

        if (accountService.hasStatus(accountId, AccountStatus.BANNED)) {
            throw new AppException(ErrorCode.ACCOUNT_BANNED);
        }

        return signedJWT;
    }

    public String generateSocketToken(String userId) {
        return redisService.createSocketToken(userId);
    }

    private String buildScope(Account account) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(account.getRoles())) {
            account.getRoles().forEach(stringJoiner::add);
        }

        return stringJoiner.toString();
    }
}