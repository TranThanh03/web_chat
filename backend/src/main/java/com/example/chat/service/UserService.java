package com.example.chat.service;

import com.example.chat.dto.request.UserRequest;
import com.example.chat.entity.User;
import com.example.chat.enums.AccountStatus;
import com.example.chat.enums.PresenceStatus;
import com.example.chat.enums.Role;
import com.example.chat.repository.UserRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    public User createUser(UserRequest request) {
        User user = new User();

        user.setFullName(request.getFullName());
        user.setAvatar(request.getAvatar());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRoles(List.of(Role.USER.name()));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRegisteredTime(TimeUtils.toUnixMillisUtcNow());
        user.setAccountStatus(AccountStatus.INACTIVATE.name());
        user.setPresenceStatus(PresenceStatus.OFFLINE.name());

        return userRepository.save(user);
    }

//    public Conversation getConversationById(String id) {
//        return conversationRepository.findById(id)
//                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXITED));
//    }

//    public List<MessageResponse> loadMessage(String roomId, Pageable pageable) {
//
//    }

    public boolean checkParticipantsValid(List<String> participantIds) {
        long existingCount = userRepository.countByIdIn(participantIds);
        return existingCount == participantIds.size();
    }

}
