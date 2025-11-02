package com.example.chat.service;

import com.example.chat.dto.response.auth.InfoAccessTokenResponse;
import com.example.chat.entity.Account;
import com.example.chat.entity.RefreshToken;
import com.example.chat.enums.AccountStatus;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenService {
    RefreshTokenRepository refreshTokenRepository;
    AuthenticationService authenticationService;
    AccountService accountService;

    public String generateRefreshToken(String accountId) {
        String token = UUID.randomUUID().toString();
        String hashedToken = DigestUtils.sha256Hex(token);

        RefreshToken refreshToken = RefreshToken.builder()
                .accountId(accountId)
                .hashedToken(hashedToken)
                .expiryTime(Instant.now().plus(Duration.ofDays(30)))
                .build();

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public String refreshAccessToken(InfoAccessTokenResponse response, String refreshToken) {
        String hashedToken = DigestUtils.sha256Hex(refreshToken);
        var result = refreshTokenRepository.findByHashedToken(hashedToken);

        if (result == null || result.getExpiryTime().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Account account = accountService.getById(response.getAccountId());

        if (account.getStatus().equals(AccountStatus.INACTIVATE.name())) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVATE);
        }

        if (account.getStatus().equals(AccountStatus.BANNED.name())) {
            throw new AppException(ErrorCode.ACCOUNT_BANNED);
        }

        return authenticationService.generateToken(account, response.getUserId());
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        String hashedToken = DigestUtils.sha256Hex(refreshToken);
        var result = refreshTokenRepository.findByHashedToken(hashedToken);

        if (result == null || result.getExpiryTime().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        refreshTokenRepository.deleteById(result.getId());
    }

    public void deleteRefreshTokenByAccountId(String accountId) {
        if (refreshTokenRepository.existsByAccountId(accountId)) {
            refreshTokenRepository.deleteAllByAccountId(accountId);
        }
    }
}