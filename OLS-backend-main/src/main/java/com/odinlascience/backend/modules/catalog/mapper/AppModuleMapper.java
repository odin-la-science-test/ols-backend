package com.odinlascience.backend.modules.catalog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.odinlascience.backend.modules.catalog.dto.AppModuleDTO;
import com.odinlascience.backend.modules.catalog.model.AppModule;

@Mapper(componentModel = "spring")
public interface AppModuleMapper {

    @Mapping(target = "locked", ignore = true)
    AppModuleDTO toDTO(AppModule appModule);
    
    default AppModuleDTO toDTO(AppModule appModule, Boolean locked) {
        AppModuleDTO dto = toDTO(appModule);
        if (locked != null) {
            if (dto == null) {
                dto = AppModuleDTO.builder().locked(locked).build();
            } else {
                dto.setLocked(locked);
            }
        }
        return dto;
    }
}