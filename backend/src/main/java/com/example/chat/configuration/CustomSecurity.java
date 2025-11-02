package com.example.chat.configuration;

import com.example.chat.dto.response.auth.InfoAccessTokenResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component("customSecurity")
public class CustomSecurity {
    public String getUserId(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken token) {
            return token.getToken().getClaim("uid");
        }

        return null;
    }

    public String getAccountId(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken token) {
            return token.getToken().getId();
        }

        return null;
    }

    public String getToken(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken token) {
            return token.getToken().toString();
        }

        return null;
    }

    public InfoAccessTokenResponse getInfoToken(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken token) {
            return InfoAccessTokenResponse.builder()
                    .token(token.getToken().toString())
                    .accountId(token.getToken().getId())
                    .userId(token.getToken().getClaim("uid"))
                    .build();
        }

        return null;
    }
}