package com.example.chat.repository;

import com.example.chat.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    long countByIdIn(List<String> ids);
}
