package com.example.chat.service;

import com.example.chat.entity.Room;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    RoomRepository roomRepository;

    public Room createRoom(String code) {
        if (roomRepository.existsByCode(code.toLowerCase())) {
            throw new AppException(ErrorCode.ROOM_EXITED);
        }

        Room room = new Room();
        room.setCode(code.toLowerCase());
        room.setMessages(new ArrayList<>());

        return roomRepository.save(room);
    }

    public Room getRoomById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXITED));
    }
}
