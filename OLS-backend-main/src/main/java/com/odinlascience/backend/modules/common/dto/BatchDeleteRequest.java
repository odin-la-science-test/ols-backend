package com.odinlascience.backend.modules.common.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour les suppressions en lot.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDeleteRequest {

    @NotEmpty
    private List<Long> ids;
}
