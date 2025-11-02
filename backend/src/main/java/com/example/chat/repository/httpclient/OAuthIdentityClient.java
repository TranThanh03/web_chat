package com.example.chat.repository.httpclient;

import com.example.chat.configuration.FeignConfig;
import com.example.chat.dto.request.auth.ExchangeTokenRequest;
import com.example.chat.dto.response.auth.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "oauth-identity", url = "${google.oauth2-base-url}", configuration = FeignConfig.class)
public interface OAuthIdentityClient {
    @PostMapping(
            value = "/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
}
