package com.example.chat.mapper;

import com.example.chat.dto.response.MessageResponse;
import com.example.chat.entity.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageResponse toMessageResponse(Message message);
}