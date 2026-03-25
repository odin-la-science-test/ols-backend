package com.odinlascience.backend.modules.bacteriology.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

class BacteriumControllerTest extends com.odinlascience.backend.AbstractControllerTest {

    @Test
    @DisplayName("GET /api/bacteria : Doit retourner la liste (E. Coli, S. Aureus...)")
    void shouldReturnAllBacteria() throws Exception {
        mockMvc.perform(get("/api/bacteria").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
            .andExpect(jsonPath("$[*].species", hasItems("Escherichia coli", "Staphylococcus aureus")));
    }

    @Test
    @DisplayName("GET /api/bacteria/1 : Doit retourner E. Coli")
    void shouldReturnBacteriumById() throws Exception {
        mockMvc.perform(get("/api/bacteria/1").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.species", is("Escherichia coli")))
            .andExpect(jsonPath("$.gram", is("NEGATIVE")))
            .andExpect(jsonPath("$.morpho", is("BACILLI")));
    }

    @Test
    @DisplayName("GET /api/bacteria/search : Doit trouver via recherche partielle")
    void shouldSearchBySpecies() throws Exception {
        mockMvc.perform(get("/api/bacteria/search?query=aureus").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].species", containsString("Staphylococcus")));
    }

    @Test
    @DisplayName("GET /identify/api/{code} : Doit identifier par code API")
    void shouldIdentifyByApiCode() throws Exception {
        mockMvc.perform(get("/api/bacteria/identify/api/5144572").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.species", is("Escherichia coli")));
    }

    @Test
    @DisplayName("POST /api/bacteria/identify : Doit identifier par profil biochimique")
    void shouldIdentifyByBiochemicalProfile() throws Exception {
        String json = "{\"gram\":\"NEGATIVE\"}";

        mockMvc.perform(post("/api/bacteria/identify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
            .with(auth())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[*].species", hasItem("Escherichia coli")));
    }

    @Test
    @DisplayName("Erreur 404 : Doit gérer les IDs inexistants")
    void shouldReturn404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/bacteria/99999").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", is("Ressource Introuvable")))
            .andExpect(jsonPath("$.message", containsString("Bactérie introuvable")));
    }

        @Test
        @DisplayName("GET /api/bacteria/search sans query : Doit retourner 400 Bad Request")
        void shouldReturnBadRequestWhenMissingQuery() throws Exception {
        mockMvc.perform(get("/api/bacteria/search").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /api/bacteria/identify/api/{code} : 404 si code inconnu")
        void shouldReturn404ForUnknownApiCode() throws Exception {
        mockMvc.perform(get("/api/bacteria/identify/api/0000000").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", is("Ressource Introuvable")))
            .andExpect(jsonPath("$.message", containsString("ne correspond au code API")));
        }
}