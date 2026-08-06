package com.hbvibe.user.service;

import com.hbvibe.user.dto.identity.Credential;
import com.hbvibe.user.dto.identity.TokenExchangeParam;
import com.hbvibe.user.dto.identity.UserCreationParam;
import com.hbvibe.user.dto.request.RegistrationRequest;
import com.hbvibe.user.dto.response.ProfileResponse;
import com.hbvibe.user.exception.AppException;
import com.hbvibe.user.exception.ErrorCode;
import com.hbvibe.user.exception.ErrorNormalizer;
import com.hbvibe.user.mapper.ProfileMapper;
import com.hbvibe.user.repository.IdentityClient;
import com.hbvibe.user.repository.ProfileRepository;
import feign.FeignException;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ErrorNormalizer errorNormalizer;
    IdentityClient identityClient;
    ProfileMapper profileMapper;

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;
    @Value("${idp.client-secret}")
    @NonFinal
    String clientSerect;

    public List<ProfileResponse> getAllUsers(){
        var userProfiles= profileRepository.findAll();
        return userProfiles.stream().map(profileMapper::toProfileResponse).toList();

    }

    public ProfileResponse getMyUserProfile(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        var myProfile =  profileRepository.findByKeycloakId(userId);
        return profileMapper.toProfileResponse(myProfile);
    }

    public ProfileResponse register(RegistrationRequest registrationRequest) {
        log.info("Dữ liệu nhận từ Frontend: {}", registrationRequest);
        try {
            if (profileRepository.existsByPhoneNumber(registrationRequest.getPhoneNumber())) {
                throw new AppException(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
            }
            // create  account  in keycloak
            //Exchange client token
            var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSerect)
                    .scope("openid")
                    .build());

            log.info("TokenInfo {}", token);
            // Create user with client Token and given info
            // Get userId of keyCloak account
            var creationResponse = identityClient.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(registrationRequest.getUsername())
                            .enabled(true)
                            .email(registrationRequest.getEmail())
                            .firstName(registrationRequest.getFirstName())
                            .lastName(registrationRequest.getLastName())
                            .credentials(
                                    List.of(Credential.builder()
                                            .type("password")
                                            .value(registrationRequest.getPassword())
                                            .temporary(false)
                                            .build())
                            )
                            .build()
            );

            String keyCloakId= extractUserId(creationResponse);
            log.info("keyCloakId {}", keyCloakId);
            var userProfile= profileMapper.toUserProfile(registrationRequest);
            userProfile.setKeycloakId(keyCloakId);
            userProfile= profileRepository.save(userProfile);

            return profileMapper.toProfileResponse(userProfile);



        } catch (FeignException exception) {
            throw  errorNormalizer.handleKeyCloakException(exception);


        }


    }

    private String extractUserId(ResponseEntity<?> response){
        String location=response.getHeaders().get("Location").getFirst();
        String [] splitedStr = location.split("/");
        return splitedStr[splitedStr.length-1];
    }
}