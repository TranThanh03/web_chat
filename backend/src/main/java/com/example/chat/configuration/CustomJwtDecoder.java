package com.example.chat.configuration;

import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    private final AuthenticationService authenticationService;
    private final String signerKey;
    private final HttpServletRequest httpServletRequest;
    private NimbusJwtDecoder nimbusJwtDecoder;

    public CustomJwtDecoder(
            AuthenticationService authenticationService,
            @Value("${jwt.signer_key}") String SIGNER_KEY,
            HttpServletRequest httpServletRequest
    ) {
        this.authenticationService = authenticationService;
        this.signerKey = SIGNER_KEY;
        this.httpServletRequest = httpServletRequest;
    }

    @PostConstruct
    private void init() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");
        this.nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            if (Objects.isNull(authenticationService.verifyToken(token))) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
        } catch (AppException ae) {
            httpServletRequest.setAttribute("errorCode", ae);
            throw new BadCredentialsException("Invalid token.");
        } catch (Exception e) {
            httpServletRequest.setAttribute("errorCode", ErrorCode.INVALID_TOKEN);
            throw new BadCredentialsException("Invalid token.");
        }

        return nimbusJwtDecoder.decode(token);
    }
}