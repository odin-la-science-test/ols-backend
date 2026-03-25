package com.odinlascience.backend.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAvatarRequest {
    @Size(max = 50)
    private String avatarId;
}
