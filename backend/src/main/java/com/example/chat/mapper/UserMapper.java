package com.example.chat.mapper;

import com.example.chat.dto.response.UserResponse;
import com.example.chat.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);

    List<UserResponse> toUserListResponse(List<User> userList);
}