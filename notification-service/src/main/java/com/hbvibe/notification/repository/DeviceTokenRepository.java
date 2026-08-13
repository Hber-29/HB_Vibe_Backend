package com.hbvibe.notification.repository;

import com.hbvibe.notification.entity.push.UserDevice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends MongoRepository<UserDevice,String> {
    Optional<UserDevice> findByUserId(String userId);
    @Query("{ 'fcmTokens.token': ?0 }")
    @Update("{ '$pull': { 'fcmTokens': { 'token': ?0 } } }")
    void deleteFcmToken(String fcmToken);
}
