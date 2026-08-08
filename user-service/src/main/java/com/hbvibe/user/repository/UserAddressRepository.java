package com.hbvibe.user.repository;

import com.hbvibe.user.entity.UserAddress;
import com.hbvibe.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, String> {
    Optional<UserAddress> findById(UUID id);
    Boolean existsById(UUID id );
    void deleteById(UUID id);
    List<UserAddress> findAllByUserProfile(UserProfile userProfile);

}
