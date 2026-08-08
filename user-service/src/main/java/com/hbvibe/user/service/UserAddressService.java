package com.hbvibe.user.service;

import com.hbvibe.user.dto.request.UserAddressRequest;
import com.hbvibe.user.dto.response.UserAddressResponse;
import com.hbvibe.user.entity.UserAddress;
import com.hbvibe.user.entity.UserProfile;
import com.hbvibe.user.exception.AppException;
import com.hbvibe.user.exception.ErrorCode;
import com.hbvibe.user.mapper.UserAddressMapper;
import com.hbvibe.user.repository.ProfileRepository;
import com.hbvibe.user.repository.UserAddressRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserAddressService {
    ProfileRepository profileRepository;
    UserAddressRepository userAddressRepository;
    UserAddressMapper userAddressMapper;

    public UserAddressResponse createUserAddress( UserAddressRequest userAddressRequest){
        log.info("Dữ liệu nhận từ addressRequest: {}", userAddressRequest);
        if(Objects.isNull(userAddressRequest)){
            throw new AppException(ErrorCode.VALUE_NULL);
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        // xem userId của token có tồn tại trong bảng UserProfile không
        if(!profileRepository.existsByKeycloakId(userId)){
            throw new AppException(ErrorCode.USERID_NOT_EXISTS);
        }
        UserAddress userAddress = UserAddress.builder()
                .userProfile(profileRepository.findByKeycloakId(userId))
                .receiverName(userAddressRequest.getReceiverName())
                .receiverPhone(userAddressRequest.getReceiverPhone())
                .cityProvince(userAddressRequest.getCityProvince())
                .district(userAddressRequest.getDistrict())
                .ward(userAddressRequest.getWard())
                .streetAddress(userAddressRequest.getStreetAddress())
                .isDefault(false)
                .build();
        userAddress= userAddressRepository.save(userAddress);
        return userAddressMapper.toUserAddressResponse(userAddress);

    }

    public UserAddressResponse  updateUserAddress(UserAddressRequest userAddressRequest, UUID id){
        log.info("Dữ liệu nhận từ addressRequest: {}", userAddressRequest);
        if(Objects.isNull(userAddressRequest)){
            throw new AppException(ErrorCode.VALUE_NULL);
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(!profileRepository.existsByKeycloakId(userId)){
            throw new AppException(ErrorCode.USERID_NOT_EXISTS);
        }
        UserAddress address=userAddressRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_EXISTS));

        address.setReceiverName(userAddressRequest.getReceiverName());
        address.setReceiverPhone(userAddressRequest.getReceiverPhone());
        address.setCityProvince(userAddressRequest.getCityProvince());
        address.setDistrict(userAddressRequest.getDistrict());
        address.setWard(userAddressRequest.getWard());
        address.setStreetAddress(userAddressRequest.getStreetAddress());
        address = userAddressRepository.save(address);
        return userAddressMapper.toUserAddressResponse(address);


    }

    public List<UserAddressResponse> getAllUserAddresses(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        UserProfile userProfile =profileRepository.findByKeycloakId(userId);
        if(Objects.isNull(userProfile)){
            throw new AppException(ErrorCode.USERID_NOT_EXISTS);
        }
        List<UserAddress> userAddress = userAddressRepository.findAllByUserProfile(userProfile);
        return userAddress.stream()
                .map(userAddressMapper::toUserAddressResponse)
                .toList();

    }
    // Dùng annotation này khi thay đổi dữ liệu nhưu delete,update ,nó giúp xin phép spring.
    @Transactional
    public void deleteUserAddress(UUID id){
        if(!userAddressRepository.existsById(id)){
            throw new AppException(ErrorCode.USERID_NOT_EXISTS);
        }
        userAddressRepository.deleteById(id);
    }
}
