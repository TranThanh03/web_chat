package com.example.chat.repository;

import com.example.chat.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    long countByIdInAndAccountStatus(List<String> ids, String status);

    boolean existsByIdAndAccountStatus(String id, String status);

    User findByPhone(String phone);

    User findByEmail(String email);
}
