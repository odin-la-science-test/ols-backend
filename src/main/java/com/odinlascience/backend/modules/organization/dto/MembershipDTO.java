package com.odinlascience.backend.modules.organization.dto;

import com.odinlascience.backend.modules.organization.enums.MembershipStatus;
import com.odinlascience.backend.modules.organization.enums.OrganizationRole;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDTO {

    private Long id;
    private Long organizationId;
    private String organizationName;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private String userAvatarId;
    private OrganizationRole role;
    private MembershipStatus status;
    private Instant joinedAt;
}
