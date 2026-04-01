package com.odinlascience.backend.modules.organization.dto;

import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRoleRequest {

    @NotNull
    private OrganizationRole role;
}
