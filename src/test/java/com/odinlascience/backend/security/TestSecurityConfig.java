package com.odinlascience.backend.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@TestConfiguration
public class TestSecurityConfig {

    public static final String TEST_USERNAME = "test@local.test";
    public static final String TEST_PASSWORD = "pwd";
    public static final String[] TEST_ROLES = new String[] {"USER"};

    public static UserDetails createTestUser() {
        return User.withUsername(TEST_USERNAME).password(TEST_PASSWORD).roles(TEST_ROLES).build();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return username -> createTestUser();
    }
}
