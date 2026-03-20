package com.odinlascience.backend.modules.catalog.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

class AppModuleControllerTest extends com.odinlascience.backend.AbstractControllerTest {

    @Test
    @DisplayName("GET /api/modules : Doit retourner la liste complète")
    void shouldReturnAllModules() throws Exception {
        mockMvc.perform(get("/api/modules").with(auth()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[?(@.moduleKey == 'MUNIN_BACTERIO')].title", hasItem("Bactériologie")))
                .andExpect(jsonPath("$[*].moduleKey", hasItems("MUNIN_BACTERIO","HUGIN_LIMS")));
    }

    @Test
    @DisplayName("GET /api/modules/type/MUNIN_ATLAS : Doit retourner les modules de l'Atlas")
    void shouldReturnAtlasModules() throws Exception {
        mockMvc.perform(get("/api/modules/type/MUNIN_ATLAS").with(auth()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].moduleKey", everyItem(startsWith("MUNIN_"))));
    }

    @Test
    @DisplayName("GET /api/modules/type/HUGIN_LAB : Doit filtrer les outils pro")
    void shouldReturnOnlyLabTools() throws Exception {
        mockMvc.perform(get("/api/modules/type/HUGIN_LAB").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[*].moduleKey", everyItem(startsWith("HUGIN_"))))
            .andExpect(jsonPath("$[*].price", everyItem(greaterThan(0.0))));
    }

    @Test
    @DisplayName("GET /api/modules/{key} : Doit retourner un module précis")
    void shouldReturnModuleByKey() throws Exception {
        mockMvc.perform(get("/api/modules/HUGIN_LIMS").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title", is("LIMS Pro")))
            .andExpect(jsonPath("$.routePath", is("/lab/lims")))
            .andExpect(jsonPath("$.moduleKey", is("HUGIN_LIMS")));
    }

        @Test
        @DisplayName("GET /api/modules/{key} : 404 si module inconnu")
        void shouldReturnNotFoundForUnknownModule() throws Exception {
        mockMvc.perform(get("/api/modules/MODULE_INEXISTANT_XYZ").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
        }
}