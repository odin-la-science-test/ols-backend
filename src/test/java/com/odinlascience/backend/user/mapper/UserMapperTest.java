package com.odinlascience.backend.user.mapper;

import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toDTO_WithValidUser_MapsCorrectly() {
        User user = User.builder()
                .id(1L)
                .email("mapper@example.com")
                .firstName("Mapper")
                .lastName("Test")
                .password("password")
                .role(RoleType.STUDENT)
                .build();

        UserDTO dto = userMapper.toDTO(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("mapper@example.com");
        assertThat(dto.getFirstName()).isEqualTo("Mapper");
        assertThat(dto.getLastName()).isEqualTo("Test");
        assertThat(dto.getRole()).isEqualTo(RoleType.STUDENT);
    }

    @Test
    void toDTO_WithNullUser_ReturnsNull() {
        UserDTO dto = userMapper.toDTO(null);
        assertThat(dto).isNull();
    }
}
