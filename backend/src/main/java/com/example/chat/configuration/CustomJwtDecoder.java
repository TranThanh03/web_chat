package com.example.chat.configuration;

import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    private final AuthenticationService authenticationService;
    private final String signerKey;
    private NimbusJwtDecoder nimbusJwtDecoder;

    public CustomJwtDecoder(
            AuthenticationService authenticationService,
            @Value("${jwt.signerKey}") String signerKey
    ) {
        this.authenticationService = authenticationService;
        this.signerKey = signerKey;
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
        if (!authenticationService.introspect(token)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        return nimbusJwtDecoder.decode(token);
    }
}