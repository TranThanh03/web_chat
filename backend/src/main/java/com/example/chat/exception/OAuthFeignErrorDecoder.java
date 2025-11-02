package com.example.chat.exception;

import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OAuthFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.status() == 400 && response.body() != null) {
                String body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);

                if (body.contains("invalid_grant")) {
                    return new AppException(ErrorCode.INVALID_AUTHORIZATE_CODE);
                }
            }
        } catch (IOException e) {
            return new RuntimeException(e);
        }

        return defaultDecoder.decode(methodKey, response);
    }
}