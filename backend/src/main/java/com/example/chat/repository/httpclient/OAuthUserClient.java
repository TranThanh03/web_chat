package com.example.chat.repository.httpclient;

import com.example.chat.dto.response.user.OAuthUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "oauth-user", url = "${google.api-base-url}")
public interface OAuthUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    OAuthUserResponse getUserInfo(@RequestParam("alt") String alt, @RequestParam("access_token") String accessToken);
}
