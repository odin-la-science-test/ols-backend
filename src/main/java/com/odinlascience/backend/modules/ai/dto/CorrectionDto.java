package com.odinlascience.backend.modules.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionDto {

    private int offset;
    private int length;
    private String message;
    private List<String> replacements;
    private String original;
}
