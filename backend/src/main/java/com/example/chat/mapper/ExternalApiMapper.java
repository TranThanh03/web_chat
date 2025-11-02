package com.example.chat.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class ExternalApiMapper {

    private final ObjectMapper snakeCaseMapper;

    public ExternalApiMapper(ObjectMapper defaultMapper) {
        this.snakeCaseMapper = defaultMapper.copy();
        this.snakeCaseMapper.setPropertyNamingStrategy(
                PropertyNamingStrategies.SNAKE_CASE
        );
    }

    public <T> T convertValue(Object source, Class<T> targetType) {
        T result = snakeCaseMapper.convertValue(source, targetType);

        if (result instanceof com.example.chat.entity.Attachment attachment
                && source instanceof Map<?, ?> map) {

            Object contextObj = map.get("context");

            if (contextObj instanceof Map<?, ?> context) {
                Object customObj = context.get("custom");

                if (customObj instanceof Map<?, ?> custom) {
                    Object filenameObj = custom.get("filename");

                    if (filenameObj instanceof String encoded) {
                        String decoded = URLDecoder.decode(
                                encoded,
                                StandardCharsets.UTF_8
                        );

                        attachment.setOriginalFilename(decoded);
                    }
                }
            }
        }

        return result;
    }
}