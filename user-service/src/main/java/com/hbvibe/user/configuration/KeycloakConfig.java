package com.hbvibe.user.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${spring.keycloak.server-url}")
    private String serverUrl;

    @Value("${spring.keycloak.admin.username}")
    private String adminUsername;

    @Value("${spring.keycloak.admin.password}")
    private String adminPassword;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId("admin-cli") // Client mặc định của Keycloak dành cho Admin
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }
}
