package com.hbvibe.user.repository;

import com.hbvibe.user.dto.request.RegistrationRequest;
import com.hbvibe.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, String> {
  UserProfile findByKeycloakId(String userId);
  Optional<UserProfile> findById(UUID id);
  Optional<UserProfile> findByIdAndKeycloakId(UUID id, String keycloakId);
  boolean existsByEmail(String email);
  boolean existsByPhoneNumber(String  phoneNumber);
  boolean existsByKeycloakId(String keycloakId);

}
