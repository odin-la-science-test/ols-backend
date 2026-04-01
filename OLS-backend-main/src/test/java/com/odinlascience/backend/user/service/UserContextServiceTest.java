package com.odinlascience.backend.user.service;

import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserContextServiceTest {

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        
        testUser = User.builder()
                .email("contexttest@example.com")
                .password("password123")
                .firstName("Context")
                .lastName("Test")
                .role(RoleType.STUDENT)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void getCurrentUser_WhenAuthenticated_ReturnsUser() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "contexttest@example.com", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<User> result = userContextService.getCurrentUser();

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("contexttest@example.com");
    }

    @Test
    void getCurrentUser_WhenNotAuthenticated_ReturnsEmpty() {
        Optional<User> result = userContextService.getCurrentUser();
        assertThat(result).isEmpty();
    }

    @Test
    void getCurrentUser_WhenAnonymous_ReturnsEmpty() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "anonymousUser", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<User> result = userContextService.getCurrentUser();
        assertThat(result).isEmpty();
    }

    @Test
    void getAuthenticatedUser_WhenAuthenticated_ReturnsUser() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "contexttest@example.com", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        User result = userContextService.getAuthenticatedUser();

        assertThat(result.getEmail()).isEqualTo("contexttest@example.com");
    }

    @Test
    void getAuthenticatedUser_WhenNotAuthenticated_ThrowsException() {
        assertThatThrownBy(() -> userContextService.getAuthenticatedUser())
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Utilisateur non authentifié");
    }

    @Test
    void getCurrentUser_WhenAuthenticationNotAuthenticated_ReturnsEmpty() {
        // Create an authentication that returns isAuthenticated() = false
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "contexttest@example.com", null, Collections.emptyList());
        auth.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<User> result = userContextService.getCurrentUser();
        assertThat(result).isEmpty();
    }
}
