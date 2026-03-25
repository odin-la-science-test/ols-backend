package com.odinlascience.backend.modules.organization.dto;

import com.odinlascience.backend.modules.organization.enums.OrganizationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrganizationRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @NotNull
    private OrganizationType type;

    @Size(max = 500)
    private String website;

    @NotBlank
    @Email
    private String ownerEmail;
}
