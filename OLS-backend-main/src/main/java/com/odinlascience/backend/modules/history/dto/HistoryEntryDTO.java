package com.odinlascience.backend.modules.history.dto;

import com.odinlascience.backend.modules.history.model.HistoryActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntryDTO {

    private Long id;
    private String moduleSlug;
    private HistoryActionType actionType;
    private Long entityId;
    private String labelKey;
    private String icon;
    private String previousData;
    private String newData;
    private Instant createdAt;
}
