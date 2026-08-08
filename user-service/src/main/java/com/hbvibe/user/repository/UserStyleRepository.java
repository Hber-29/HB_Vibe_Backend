package com.hbvibe.user.repository;

import com.hbvibe.user.entity.UserAddress;
import com.hbvibe.user.entity.UserStyleProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserStyleRepository extends JpaRepository<UserStyleProfile, String> {
    Optional<UserStyleProfile> findById(UUID id);

}
