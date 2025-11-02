package com.example.chat.service;

import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisService {
    StringRedisTemplate redisTemplate;

    private static final String INVALID_TOKEN_PREFIX = "invalid:token:";
    private static final String CHAT_TOKEN_SOCKET_PREFIX = "chat:token:socket:";
    private static final String PRESENCE_ONLINE_USERS_PREFIX = "presence:online:users";
    private static final String PRESENCE_USER_PREFIX = "presence:user:";
    private static final String PRESENCE_SOCKET_PREFIX = "presence:socket:";

    @NonFinal
    @Value("${redis-ttl.token-socket-ttl}")
    private long TOKEN_SOCKET_TTL;

    @NonFinal
    @Value("${redis-ttl.presence-socket-ttl}")
    private long PRESENCE_SOCKET_TTL;

    public void setInvalidToken(String jti, long ttl) {
        try {
            redisTemplate.opsForValue().set(INVALID_TOKEN_PREFIX + jti, "", Duration.ofSeconds(ttl));
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public boolean isInvalidToken(String jti) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(INVALID_TOKEN_PREFIX + jti));
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public String createSocketToken(String userId) {
        try {
            String socketToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(CHAT_TOKEN_SOCKET_PREFIX + socketToken, userId, Duration.ofSeconds(TOKEN_SOCKET_TTL));

            return socketToken;
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public String getUserIdBySocketToken(String socketToken) {
        try {
            return redisTemplate.opsForValue().get(CHAT_TOKEN_SOCKET_PREFIX + socketToken);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public void removeSocketToken(String socketToken) {
        try {
            redisTemplate.delete(CHAT_TOKEN_SOCKET_PREFIX + socketToken);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public boolean isValidSocketToken(String socketToken) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(CHAT_TOKEN_SOCKET_PREFIX + socketToken));
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public void registerPresence(String userId, String socketId) {
        redisTemplate.opsForSet().add(PRESENCE_USER_PREFIX + userId, socketId);
        redisTemplate.opsForSet().add(PRESENCE_ONLINE_USERS_PREFIX, userId);
        redisTemplate.opsForValue().set(PRESENCE_SOCKET_PREFIX + socketId, "", Duration.ofSeconds(PRESENCE_SOCKET_TTL));
    }

    public void heartbeatPresence(String socketId) {
        String key = PRESENCE_SOCKET_PREFIX + socketId;

        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.opsForValue().set(key, "", Duration.ofSeconds(PRESENCE_SOCKET_TTL));
        }
    }

    public void removePresence(String userId, String socketId) {
        redisTemplate.opsForSet().remove(PRESENCE_USER_PREFIX + userId, socketId);
        redisTemplate.delete(PRESENCE_SOCKET_PREFIX + socketId);

        Long size = redisTemplate.opsForSet().size(PRESENCE_USER_PREFIX + userId);
        if (size == null || size == 0) {
            redisTemplate.opsForSet().remove(PRESENCE_ONLINE_USERS_PREFIX, userId);
        }
    }

    public boolean isOnline(String userId) {
        Boolean isMember = redisTemplate.opsForSet().isMember(PRESENCE_ONLINE_USERS_PREFIX, userId);
        return Boolean.TRUE.equals(isMember);
    }
}