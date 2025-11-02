package com.example.chat.repository;

import com.example.chat.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    RefreshToken findByHashedToken(String hashedToken);

    Boolean existsByAccountId(String accountId);

    @Transactional
    void deleteAllByAccountId(String accountId);
}