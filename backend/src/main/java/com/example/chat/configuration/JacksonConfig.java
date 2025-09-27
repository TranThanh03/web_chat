package com.example.chat.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {
    @Bean
    public SimpleModule stringTrimmerModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new JsonDeserializer<>() {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                return value != null ? value.trim() : null;
            }
        });

        return module;
    }
}