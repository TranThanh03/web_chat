package com.example.chat.service;

import com.example.chat.dto.request.MessageRequest;
import com.example.chat.dto.response.MessageResponse;
import com.example.chat.entity.Message;
import com.example.chat.entity.Room;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.RoomRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    RoomRepository roomRepository;
    RoomService roomService;
    MessageMapper messageMapper;

    public MessageResponse sendMessage(String id, MessageRequest request) {
        Room room = roomService.getRoomById(id);
        Message message = new Message();

        message.setContent(request.getContent());
        message.setSender(request.getSender());
        message.setTimeStamp(TimeUtils.toUnixMillisUtcNow());

        room.getMessages().add(message);
        roomRepository.save(room);

        return messageMapper.toMessageResponse(message);
    }
}
