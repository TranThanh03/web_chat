package com.example.chat.repository;

import com.example.chat.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, String> {
    boolean existsByEmail(String email);

    boolean existsByIdAndStatus(String id, String status);

    Account findByEmail(String email);
}
