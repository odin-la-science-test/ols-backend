package com.odinlascience.backend.modules.organization.dto;

import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberRequest {

    @NotBlank
    @Email
    private String email;

    @NotNull
    private OrganizationRole role;
}
