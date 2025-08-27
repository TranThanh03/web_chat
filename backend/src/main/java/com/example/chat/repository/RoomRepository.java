package com.example.chat.repository;

import com.example.chat.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {
    Boolean existsByCode(String code);
}
