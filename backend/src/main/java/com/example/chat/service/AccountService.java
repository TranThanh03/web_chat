package com.example.chat.service;

import com.example.chat.dto.request.account.AccountCreationRequest;
import com.example.chat.dto.request.account.LocalAccountRequest;
import com.example.chat.dto.request.account.OAuthAccountRequest;
import com.example.chat.dto.request.user.UserCreationRequest;
import com.example.chat.dto.response.account.LocalAccountResponse;
import com.example.chat.dto.response.account.OAuthAccountResponse;
import com.example.chat.dto.response.user.UserCreationResponse;
import com.example.chat.entity.Account;
import com.example.chat.enums.AccountStatus;
import com.example.chat.enums.ProviderType;
import com.example.chat.enums.UserRole;
import com.example.chat.exception.AppException;
import com.example.chat.exception.ErrorCode;
import com.example.chat.repository.AccountRepository;
import com.example.chat.util.TimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    AccountRepository accountRepository;
    PasswordEncoder passwordEncoder;
    UserService userService;

    public Account create(AccountCreationRequest request) {
        Account account = new Account();

        account.setEmail(request.getEmail());
        account.setProvider(request.getProvider());
        account.setStatus(request.getStatus());
        account.setCreatedAt(TimeUtils.toUnixMillisUtcNow());
        account.setRoles(List.of(UserRole.USER.name()));

        if (request.getProvider().equals(ProviderType.LOCAL.name())) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return accountRepository.save(account);
    }

    public LocalAccountResponse createLocal(LocalAccountRequest request) {
        if (hasEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String accountStatus = AccountStatus.INACTIVATE.name();
        LocalDate dob = request.getDateOfBirth();

        if (dob.plusYears(13).isAfter(LocalDate.now())) {
            throw new AppException(ErrorCode.USER_UNDER_13);
        }

        if (dob.isBefore(LocalDate.now().minusYears(120))) {
            throw new AppException(ErrorCode.DATE_OF_BIRTH_TOO_OLD);
        }

        Account account = this.create(
                AccountCreationRequest.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .provider(ProviderType.LOCAL.name())
                        .status(accountStatus)
                        .build()
        );

        UserCreationResponse user = userService.create(
                UserCreationRequest.builder()
                        .accountId(account.getId())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .dateOfBirth(dob)
                        .accountStatus(accountStatus)
                        .build()
        );

        return LocalAccountResponse.builder()
                .id(account.getId())
                .uid(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .email(account.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .provider(account.getProvider())
                .status(account.getStatus())
                .roles(account.getRoles())
                .createAt(account.getCreatedAt())
                .build();
    }

    public OAuthAccountResponse createOAuth(OAuthAccountRequest request) {
        String accountStatus = AccountStatus.ACTIVATE.name();

        Account account = this.create(
                AccountCreationRequest.builder()
                        .email(request.getEmail())
                        .provider(ProviderType.GOOGLE.name())
                        .status(accountStatus)
                        .build()
        );

        UserCreationResponse user = userService.create(
                UserCreationRequest.builder()
                        .accountId(account.getId())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .avatar(request.getAvatar())
                        .accountStatus(accountStatus)
                        .build()
        );

        return OAuthAccountResponse.builder()
                .account(account)
                .userId(user.getId())
                .build();
    }

    public Account getById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    public Account getByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public boolean hasEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    public boolean hasStatus(String id, AccountStatus status) {
        return accountRepository.existsByIdAndStatus(id, status.name());
    }

    public void active(String id) {
        Account account = this.getById(id);

        if (!AccountStatus.INACTIVATE.name().equals(account.getStatus())) {
            throw new AppException(ErrorCode.ACTION_NOT_ALLOWED);
        }

        String status = AccountStatus.ACTIVATE.name();

        account.setStatus(status);
        accountRepository.save(account);

        userService.updateAccountStatus(id, status);
    }

//    public void updateContact(String id, AccountContactUpdateRequest request) {
//        Account account = getAccountById(id);
//
//        account.setPhone(request.getPhone());
//        account.setEmail(request.getEmail());
//
//        accountRepository.save(account);
//    }
//
//    public void changePassword(String id, PasswordChangeRequest request) {
//        if (request.getNewPassword().equals(request.getCurrentPassword())) {
//            throw new AppException(ErrorCode.PASSWORD_SAME_AS_OLD);
//        }
//
//        Account account = getAccountById(id);
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
//
//        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
//            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
//        }
//
//        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
//
//        accountRepository.save(account);
//    }
//
//    public void resetPassword(String id, PasswordRequest request) {
//        Account account = getAccountById(id);
//
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
//        account.setPassword(passwordEncoder.encode(request.getPassword()));
//
//        accountRepository.save(account);
//    }
}