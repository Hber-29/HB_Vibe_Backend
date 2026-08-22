package com.hbvibe.brand.service.keycloak;


import com.hbvibe.brand.exception.AppException;
import com.hbvibe.brand.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.core.annotation.MergedAnnotations.search;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class KeycloakGroupService {
    Keycloak keycloak;
    @Value("${spring.keycloak.realm}")
    @NonFinal
    String realm;

    public void addUserGruop(String userId, String gruopName){
        try{
            String gruopId= keycloak.realm(realm).groups().groups()
                    .stream().filter(g -> g.getName().equalsIgnoreCase(gruopName))
                    .findFirst()
                    .orElseThrow(()-> new AppException(ErrorCode.FIND_NOT_GRUOP))
                    .getId();
            keycloak.realm(realm).users().get(userId).joinGroup(gruopId);
        }catch (Exception e){
            log.error("Lỗi khi kết nối Keycloak: ", e);
            throw new RuntimeException("Lỗi cấp quyền trên hệ thống Keycloak!");

        }
    }

    public List<UserRepresentation> searchUser(String staffEmail){
        List<UserRepresentation> users = keycloak.realm(realm).users().search(null, null, null, staffEmail, 0, 10);
        return users;
    }
}
