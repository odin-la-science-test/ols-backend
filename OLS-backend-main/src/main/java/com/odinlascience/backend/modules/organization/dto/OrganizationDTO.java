package com.odinlascience.backend.modules.organization.dto;

import com.odinlascience.backend.modules.organization.enums.OrganizationType;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {

    private Long id;
    private String name;
    private String description;
    private OrganizationType type;
    private String website;
    private Instant createdAt;
    private int memberCount;
    private String createdByName;
}
