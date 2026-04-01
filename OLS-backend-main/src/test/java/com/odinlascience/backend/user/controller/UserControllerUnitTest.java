package com.odinlascience.backend.user.controller;

import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.mapper.UserMapper;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.service.UserContextService;
import com.odinlascience.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserService service;

    @Mock
    private UserContextService userContextService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private UserController userController;

    @Test
    void getCurrentUser_WhenNoUserInContext_Returns401() {
        // Arrange
        when(httpServletRequest.getRequestURI()).thenReturn("/api/users/me");
        // Arrange
        when(userContextService.getCurrentUser()).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = userController.getCurrentUser(httpServletRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getCurrentUser_WhenUserInContext_ReturnsUser() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
        
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        when(userContextService.getCurrentUser()).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // Act
        ResponseEntity<?> response = userController.getCurrentUser(httpServletRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userDTO);
    }
}
