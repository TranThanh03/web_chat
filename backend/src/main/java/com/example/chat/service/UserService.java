package com.example.chat.service;

import com.example.chat.dto.request.*;
import com.example.chat.entity.User;
import com.example.chat.enums.AccountStatus;
import com.example.chat.enums.PresenceStatus;
import com.example.chat.enums.UserRole;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.UserRepository;
import com.example.chat.util.CodeGenerator;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    @NonFinal
    @Value("${base.defaultAvatarUrl}")
    protected String DEFAULT_AVATAR_URL;

    UserRepository userRepository;

    @Retryable(
            retryFor = DuplicateKeyException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public User createUser(UserCreationRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = new User();
        user.setCode(generateCode(request.getFullName()));
        user.setFullName(request.getFullName());
        user.setAvatar(DEFAULT_AVATAR_URL);
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRoles(List.of(UserRole.USER.name()));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRegisteredTime(TimeUtils.toUnixMillisUtcNow());
        user.setAccountStatus(AccountStatus.INACTIVATE.name());
        user.setPresenceStatus(PresenceStatus.OFFLINE.name());

        return userRepository.save(user);
    }

    @Retryable(
            retryFor = DuplicateKeyException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public User createUserOutbound(UserCreationOutboundRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = new User();
        user.setCode(generateCode(request.getFullName()));
        user.setFullName(request.getFullName());
        user.setAvatar(request.getAvatar());
        user.setEmail(request.getEmail());
        user.setRoles(List.of(UserRole.USER.name()));
        user.setRegisteredTime(TimeUtils.toUnixMillisUtcNow());
        user.setAccountStatus(AccountStatus.ACTIVATE.name());
        user.setPresenceStatus(PresenceStatus.OFFLINE.name());

        return userRepository.save(user);
    }

    public void createPassword(String id, PasswordRequest request) {
        User user = getUserById(id);

        if (StringUtils.hasText(user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_EXISTED);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITED));
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

    public boolean checkParticipantsValid(List<String> participantIds) {
        long existingCount = userRepository.countByIdInAndAccountStatus(participantIds, AccountStatus.ACTIVATE.name());

        return existingCount == participantIds.size();
    }

    public void verifyActiveAccount(String id) {
        if (!userRepository.existsByIdAndAccountStatus(id, AccountStatus.ACTIVATE.name())) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
    }

    public void activeAccount(String id) {
        User user = getUserById(id);

        if (!AccountStatus.INACTIVATE.name().equals(user.getAccountStatus())) {
            throw new AppException(ErrorCode.USER_NOT_EXITED);
        }

        user.setAccountStatus(AccountStatus.ACTIVATE.name());

        userRepository.save(user);
    }

    public void updateUserInfo(String id, UserInfoUpdateRequest request) {
        User user = getUserById(id);

        user.setFullName(request.getFullName());
        user.setAvatar(request.getAvatar());

        userRepository.save(user);
    }

    public void updateUserContact(String id, UserContactUpdateRequest request) {
        User user = getUserById(id);

        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        userRepository.save(user);
    }

    public void changePassword(String id, PasswordChangeRequest request) {
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new AppException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        User user = getUserById(id);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_CURRENT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    public void forgotPassword(String id, PasswordRequest request) {
        User user = getUserById(id);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
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
