package com.example.chat.service;

import com.example.chat.dto.request.UserRequest;
import com.example.chat.entity.User;
import com.example.chat.enums.AccountStatus;
import com.example.chat.enums.PresenceStatus;
import com.example.chat.enums.Role;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.UserRepository;
import com.example.chat.util.CodeGenerator;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    @Retryable(
            retryFor = DuplicateKeyException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public User createUser(UserRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = new User();

        user.setCode(generateCode(request.getFullName()));
        user.setFullName(request.getFullName().trim());
        user.setAvatar(request.getAvatar().trim());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRoles(List.of(Role.USER.name()));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));

        user.setRegisteredTime(TimeUtils.toUnixMillisUtcNow());
        user.setAccountStatus(AccountStatus.INACTIVATE.name());
        user.setPresenceStatus(PresenceStatus.OFFLINE.name());

        return userRepository.save(user);
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITED));
    }

    public boolean checkParticipantsValid(List<String> participantIds) {
        long existingCount = userRepository.countByIdIn(participantIds);

        return existingCount == participantIds.size();
    }

    public void validateActiveUserExists(String id) {
        if(!userRepository.existsByIdAndAccountStatus(id, AccountStatus.ACTIVATE.name())) {
            throw new AppException(ErrorCode.USER_NOT_EXITED);
        }
    }

    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isCustomerBanned(String id) {
        return userRepository.existsByIdAndAccountStatus(id, AccountStatus.BANNED.name());
    }

    private static String generateCode(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "";
        }

        String result = fullName.trim().toLowerCase();
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = result.replaceAll("\\p{M}", "");
        result = result.replace("đ", "d");
        result = result.replaceAll("\\s+", ".");

        return String.format("%s.%s",result, CodeGenerator.generateNumericCode());
    }
}
