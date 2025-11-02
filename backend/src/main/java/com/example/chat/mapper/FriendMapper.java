package com.example.chat.mapper;

import com.example.chat.dto.response.friend.FriendResponse;
import com.example.chat.entity.Friend;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FriendMapper {
    List<FriendResponse> toFriendListResponse(List<Friend> friends);
}
