package com.example.chat.repository;

import com.example.chat.dto.response.user.UserInfoResponse;
import com.example.chat.entity.User;
import com.example.chat.projection.UserNotifyView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    long countByIdInAndAccountStatus(List<String> ids, String status);

    boolean existsByIdAndAccountStatus(String id, String status);

    @Query(value = "{ 'accountId': ?0 }", fields = "{ '_id': 1 }")
    Optional<User> findIdByAccountId(String accountId);

    Optional<UserInfoResponse> findUserInfoById(String id);

    @Query("{ 'accountId': ?0 }")
    @Update("{ '$set': { 'accountStatus': ?1 } }")
    void updateAccountStatus(String accountId, String status);

    boolean existsByCode(String code);

    UserNotifyView findUserNotifyById(String id);
}
