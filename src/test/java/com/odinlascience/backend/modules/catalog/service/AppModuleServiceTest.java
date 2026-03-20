package com.odinlascience.backend.modules.catalog.service;

import com.odinlascience.backend.exception.ResourceNotFoundException;
import com.odinlascience.backend.modules.catalog.dto.AppModuleDTO;
import com.odinlascience.backend.modules.catalog.enums.ModuleType;
import com.odinlascience.backend.modules.catalog.model.AppModule;
import com.odinlascience.backend.modules.catalog.repository.AppModuleRepository;
import com.odinlascience.backend.modules.catalog.mapper.AppModuleMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppModuleServiceTest {

    @Mock
    private AppModuleRepository repository;

    @Mock
    private ModuleAccessService accessService;

    private AppModuleMapper mapper;

    private AppModuleService service;

    private AppModule m1;

    @BeforeEach
    void setUp() {
        mapper = org.mapstruct.factory.Mappers.getMapper(AppModuleMapper.class);
        service = new AppModuleService(repository, mapper, accessService);
        org.mockito.Mockito.lenient().when(accessService.isModuleLocked(any())).thenReturn(false);
        m1 = AppModule.builder()
                .id(1L)
                .moduleKey("mod-key")
                .title("Module Title")
                .icon("icon-name")
                .description("desc")
                .routePath("/path")
                .price(12.5)
                .type(ModuleType.MUNIN_ATLAS)
                .build();
    }

    @Test
    void getAllModulesMapsToDTO() {
        when(repository.findAll()).thenReturn(List.of(m1));

        List<AppModuleDTO> dtos = service.getAllModules();

        assertEquals(1, dtos.size());
        AppModuleDTO dto = dtos.get(0);
        assertEquals(m1.getModuleKey(), dto.getModuleKey());
        assertEquals(m1.getTitle(), dto.getTitle());
        assertEquals(m1.getIcon(), dto.getIcon());
        assertEquals(m1.getDescription(), dto.getDescription());
        assertEquals(m1.getRoutePath(), dto.getRoutePath());
        assertEquals(m1.getPrice(), dto.getPrice());
        assertFalse(dto.isLocked());
    }

    @Test
    void getModulesByTypeFiltersAndMaps() {
        when(repository.findByType(ModuleType.MUNIN_ATLAS)).thenReturn(List.of(m1));

        List<AppModuleDTO> dtos = service.getModulesByType(ModuleType.MUNIN_ATLAS);

        assertEquals(1, dtos.size());
        assertEquals("mod-key", dtos.get(0).getModuleKey());
    }

    @Test
    void getModuleByKeyFound() {
        when(repository.findByModuleKey("mod-key")).thenReturn(java.util.Optional.of(m1));

        AppModuleDTO dto = service.getModuleByKey("mod-key");

        assertEquals("mod-key", dto.getModuleKey());
        assertEquals("Module Title", dto.getTitle());
    }

    @Test
    void getModuleByKeyNotFoundThrows() {
        when(repository.findByModuleKey("nope")).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getModuleByKey("nope"));
    }

}
