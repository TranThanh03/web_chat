package com.example.chat.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecaptchaService {
    @NonFinal
    @Value("${recaptcha.verify-url}")
    String RECAPTCHA_VERIFY_URL;

    @NonFinal
    @Value("${recaptcha.cb-secret}")
    String RECAPTCHA_CB_SECRET;

    @NonFinal
    @Value("${recaptcha.inv-secret}")
    String RECAPTCHA_INV_SECRET;

    public boolean verifyCB(String token) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = Map.of(
                "secret", RECAPTCHA_CB_SECRET,
                "response", token
        );

        Map<String, Object> resp = restTemplate.postForObject(
                RECAPTCHA_VERIFY_URL + "?secret={secret}&response={response}",
                null, Map.class, body
        );

        return resp != null && (Boolean) resp.get("success");
    }

    public boolean verifyINV(String token) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = Map.of(
                "secret", RECAPTCHA_INV_SECRET,
                "response", token
        );

        Map<String, Object> resp = restTemplate.postForObject(
                RECAPTCHA_VERIFY_URL + "?secret={secret}&response={response}",
                null, Map.class, body
        );

        return resp != null && (Boolean) resp.get("success");
    }
}