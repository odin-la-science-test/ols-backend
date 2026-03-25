package com.odinlascience.backend.modules.organization.dto;

import com.odinlascience.backend.modules.organization.enums.OrganizationType;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrganizationRequest {

    @Size(max = 255)
    private String name;

    private String description;

    private OrganizationType type;

    @Size(max = 500)
    private String website;
}
