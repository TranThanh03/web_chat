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
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisService {
    StringRedisTemplate redisTemplate;

    private static final String INVALID_TOKEN_PREFIX = "invalid:token:";
    private static final String SOCKET_TOKEN_PREFIX = "chat:socket:token:";
    private static final String USER_SOCKET_PREFIX = "user:sockets:";
    private static final String SOCKET_USER_PREFIX = "socket:user:";
    private static final String USER_PRESENCE_PREFIX = "user:presence:";

    @NonFinal
    @Value("${redis-ttl.socket-token-ttl}")
    private long SOCKET_TOKEN_TTL;

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
            redisTemplate.opsForValue().set(SOCKET_TOKEN_PREFIX + socketToken, userId, Duration.ofSeconds(SOCKET_TOKEN_TTL));

            return socketToken;
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public String getUserIdBySocketToken(String socketToken) {
        try {
            return redisTemplate.opsForValue().get(SOCKET_TOKEN_PREFIX + socketToken);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public void removeSocketToken(String socketToken) {
        try {
            redisTemplate.delete(SOCKET_TOKEN_PREFIX + socketToken);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public boolean isValidSocketToken(String socketToken) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(SOCKET_TOKEN_PREFIX + socketToken));
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public void addSocketForUser(String userId, String socketId) {
        try {
            redisTemplate.opsForSet().add(USER_SOCKET_PREFIX + userId, socketId);
            redisTemplate.opsForValue().set(SOCKET_USER_PREFIX + socketId, userId);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public void removeSocketForUser(String userId, String socketId) {
        try {
            redisTemplate.opsForSet().remove(USER_SOCKET_PREFIX + userId, socketId);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public Set<String> getAllSocketsOfUser(String userId) {
        try {
            return redisTemplate.opsForSet().members(USER_SOCKET_PREFIX + userId);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public String getUserBySocket(String socketId) {
        try {
            return redisTemplate.opsForValue().get(SOCKET_USER_PREFIX + socketId);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public boolean hasActiveSockets(String userId) {
        try {
            Set<String> sockets = redisTemplate.opsForSet().members(USER_SOCKET_PREFIX + userId);
            return sockets != null && !sockets.isEmpty();
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public void markUserOnline(String userId) {
        try {
            redisTemplate.opsForValue().set(USER_PRESENCE_PREFIX + userId, "online");
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public void markUserOffline(String userId) {
        try {
            redisTemplate.opsForValue().set(USER_PRESENCE_PREFIX + userId, "offline");
            redisTemplate.delete(USER_SOCKET_PREFIX + userId);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    public void deleteSocketMapping(String socketId) {
        try {
            redisTemplate.delete(SOCKET_USER_PREFIX + socketId);
        } catch (Exception e) {
            log.error("Redis error: ", e);
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }
}