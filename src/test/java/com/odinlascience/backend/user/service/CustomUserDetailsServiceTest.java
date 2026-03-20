package com.odinlascience.backend.user.service;

import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("detailstest@example.com")
                .password("password123")
                .firstName("Details")
                .lastName("Test")
                .role(RoleType.ADMIN)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void loadUserByUsername_WithValidEmail_ReturnsUserDetails() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("detailstest@example.com");

        assertThat(userDetails.getUsername()).isEqualTo("detailstest@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_WithInvalidEmail_ThrowsException() {
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("notexists@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Utilisateur introuvable");
    }
}
