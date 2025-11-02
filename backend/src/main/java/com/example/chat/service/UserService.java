package com.example.chat.service;

import com.example.chat.dto.request.user.UserCreationRequest;
import com.example.chat.dto.request.user.UserInfoUpdateRequest;
import com.example.chat.dto.response.user.UserCreationResponse;
import com.example.chat.dto.response.user.UserInfoResponse;
import com.example.chat.entity.User;
import com.example.chat.enums.AccountStatus;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.mapper.UserMapper;
import com.example.chat.projection.UserNotifyView;
import com.example.chat.repository.UserRepository;
import com.example.chat.util.CodeGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    @NonFinal
    @Value("${base.default-avatar-url}")
    protected String DEFAULT_AVATAR_URL;

    UserRepository userRepository;
    UserMapper userMapper;

    @Retryable(
            retryFor = DuplicateKeyException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public UserCreationResponse create(UserCreationRequest request) {
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String fullName = String.format("%s %s", firstName, lastName);
        String avatar = request.getAvatar();
        User user = new User();

        user.setAccountId(request.getAccountId());
        user.setCode(generateCode(fullName));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setFullName(fullName);
        user.setAvatar(avatar == null || avatar.isBlank() ? DEFAULT_AVATAR_URL : avatar);
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAccountStatus(request.getAccountStatus());

        return userMapper.toUserCreationResponse(userRepository.save(user));
    }

    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public UserInfoResponse getInfoById(String id) {
        return userRepository.findUserInfoById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public String getIdByAccountId(String accountId) {
        return userRepository.findIdByAccountId(accountId)
                .map(User::getId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public UserNotifyView getUserNotifyById(String id) {
        return userRepository.findUserNotifyById(id);
    }

    public boolean checkParticipantsValid(List<String> participantIds) {
        long existingCount = userRepository.countByIdInAndAccountStatus(participantIds, AccountStatus.ACTIVATE.name());

        return existingCount == participantIds.size();
    }

    public void verifyActiveAccount(String id) {
        User user = this.getById(id);

        if (!user.getAccountStatus().equals(AccountStatus.ACTIVATE.name())) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
    }

    public void checkExists(String id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
    }

    public void updateInfo(String id, UserInfoUpdateRequest request) {
        String code = request.getCode();
        LocalDate dob = request.getDateOfBirth();
        User user = getById(id);

        if (!user.getCode().equals(request.getCode())) {
            if (userRepository.existsByCode(code)) {
                throw new AppException(ErrorCode.CODE_ALREADY_EXISTS);
            }

            user.setCode(code);
        }

        if (!user.getDateOfBirth().isEqual(request.getDateOfBirth())) {
            if (dob.plusYears(13).isAfter(LocalDate.now())) {
                throw new AppException(ErrorCode.USER_UNDER_13);
            }

            if (dob.isBefore(LocalDate.now().minusYears(120))) {
                throw new AppException(ErrorCode.DATE_OF_BIRTH_TOO_OLD);
            }

            user.setDateOfBirth(dob);
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAvatar(request.getAvatar());

        userRepository.save(user);
    }

    public void updateAccountStatus(String accountId, String status) {
        userRepository.updateAccountStatus(accountId, status);
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
