package com.example.chat.mapper;

import com.example.chat.dto.response.user.UserCreationResponse;
import com.example.chat.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserCreationResponse toUserCreationResponse(User user);
}