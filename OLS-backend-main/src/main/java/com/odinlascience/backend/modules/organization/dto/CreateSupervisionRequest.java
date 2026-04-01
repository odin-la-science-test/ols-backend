package com.odinlascience.backend.modules.organization.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupervisionRequest {

    @NotNull
    private Long supervisorId;

    @NotNull
    private Long superviseeId;
}
