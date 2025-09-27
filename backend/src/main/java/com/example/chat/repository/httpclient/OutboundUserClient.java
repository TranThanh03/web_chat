package com.example.chat.repository.httpclient;

import com.example.chat.dto.response.ExchangeTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-user", url = "${google.apiBaseUrl}")
public interface OutboundUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    ExchangeTokenResponse getUserInfo(@RequestParam("alt") String alt, @RequestParam("access_token") String accessToken);
}
