package com.example.chat.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component("customSecurity")
public class CustomSecurity {
    public String getUserId(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken token) {
            return token.getToken().getClaim("userId");
        }

        return null;
    }
}