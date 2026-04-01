package com.odinlascience.backend.modules.history.mapper;

import com.odinlascience.backend.modules.history.dto.HistoryEntryDTO;
import com.odinlascience.backend.modules.history.model.HistoryEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoryEntryMapper {

    @Mapping(source = "createdAt", target = "createdAt")
    HistoryEntryDTO toDTO(HistoryEntry entity);
}
