package com.odinlascience.backend.user.service;

import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("servicetest@example.com")
                .password("password123")
                .firstName("Service")
                .lastName("Test")
                .role(RoleType.STUDENT)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void getAllUsers_ReturnsAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        assertThat(users).isNotEmpty();
    }

    @Test
    void getUserById_WithValidId_ReturnsUser() {
        UserDTO user = userService.getUserById(testUser.getId());
        assertThat(user.getEmail()).isEqualTo("servicetest@example.com");
    }

    @Test
    void getUserById_WithInvalidId_ThrowsException() {
        assertThatThrownBy(() -> userService.getUserById(99999L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getUserByEmail_WithValidEmail_ReturnsUser() {
        UserDTO user = userService.getUserByEmail("servicetest@example.com");
        assertThat(user.getEmail()).isEqualTo("servicetest@example.com");
    }

    @Test
    void getUserByEmail_WithInvalidEmail_ThrowsException() {
        assertThatThrownBy(() -> userService.getUserByEmail("notexists@example.com"))
                .isInstanceOf(ResponseStatusException.class);
    }
}
