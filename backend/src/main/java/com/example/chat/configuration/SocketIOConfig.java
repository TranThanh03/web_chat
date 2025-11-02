package com.example.chat.configuration;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.example.chat.service.RedisService;
import com.example.chat.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SocketIOConfig {
    @NonFinal
    @Value("${base.fe_url}")
    protected String BASE_FE_URL;

    RedisService redisService;
    UserService userService;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration configuration = new com.corundumstudio.socketio.Configuration();

        configuration.setPort(8081);
        configuration.setOrigin(BASE_FE_URL);
        configuration.setAuthorizationListener(createAuthorizationListener());

        return new SocketIOServer(configuration);
    }

    private AuthorizationListener createAuthorizationListener() {
        return data -> {
            String token = data.getSingleUrlParam("token");

            if (token == null || token.isEmpty()) {
                return AuthorizationResult.FAILED_AUTHORIZATION;
            }

            try {
                var valid = redisService.isValidSocketToken(token);

                if (valid) {
                    String userId = redisService.getUserIdBySocketToken(token);
                    data.getHttpHeaders().add("userId", userId);
                    redisService.removeSocketToken(token);

                    try {
                        userService.verifyActiveAccount(userId);
                    } catch (Exception e) {
                        return AuthorizationResult.FAILED_AUTHORIZATION;
                    }

                    return AuthorizationResult.SUCCESSFUL_AUTHORIZATION;
                } else {
                    return AuthorizationResult.FAILED_AUTHORIZATION;
                }
            } catch (Exception e) {
                log.error("Auth socket failed: ", e);
                return AuthorizationResult.FAILED_AUTHORIZATION;
            }
        };
    }

    @Bean
    public SocketIONamespace appNamespace(SocketIOServer server) {
        return server.addNamespace("/ws");
    }
}
