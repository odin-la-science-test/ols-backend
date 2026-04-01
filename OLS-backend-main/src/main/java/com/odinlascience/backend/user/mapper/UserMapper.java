package com.odinlascience.backend.user.mapper;

import com.odinlascience.backend.user.dto.UserDTO;
import com.odinlascience.backend.user.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDTO toDTO(User user);
}