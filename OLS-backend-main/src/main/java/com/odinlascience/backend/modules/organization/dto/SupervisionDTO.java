package com.odinlascience.backend.modules.organization.dto;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisionDTO {

    private Long id;
    private Long organizationId;
    private Long supervisorId;
    private String supervisorName;
    private String supervisorAvatarId;
    private Long superviseeId;
    private String superviseeName;
    private String superviseeAvatarId;
    private Instant createdAt;
}
