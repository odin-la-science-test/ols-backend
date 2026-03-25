package com.odinlascience.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPreferencesRequest {

    @NotBlank
    private String preferencesJson;

    @NotNull
    private Instant lastModified;

    private int version;
}
