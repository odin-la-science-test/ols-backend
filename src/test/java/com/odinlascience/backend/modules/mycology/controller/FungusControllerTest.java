package com.odinlascience.backend.modules.mycology.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

class FungusControllerTest extends com.odinlascience.backend.AbstractControllerTest {

    @Test
    @DisplayName("GET /api/fungi : Doit retourner la liste (Saccharomyces, Candida...)")
    void shouldReturnAllFungi() throws Exception {
        mockMvc.perform(get("/api/fungi").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
            .andExpect(jsonPath("$[*].species", hasItems("Saccharomyces cerevisiae", "Candida albicans")));
    }

    @Test
    @DisplayName("GET /api/fungi/1 : Doit retourner Saccharomyces cerevisiae")
    void shouldReturnFungusById() throws Exception {
        mockMvc.perform(get("/api/fungi/1").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.species", is("Saccharomyces cerevisiae")))
            .andExpect(jsonPath("$.type", is("LEVURES")))
            .andExpect(jsonPath("$.category", is("FERMENTATION")));
    }

    @Test
    @DisplayName("GET /api/fungi/search : Doit trouver via recherche partielle")
    void shouldSearchBySpecies() throws Exception {
        mockMvc.perform(get("/api/fungi/search?query=Candida").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].species", containsString("Candida")));
    }

    @Test
    @DisplayName("GET /identify/api/{code} : Doit identifier par code API")
    void shouldIdentifyByApiCode() throws Exception {
        mockMvc.perform(get("/api/fungi/identify/api/SAC001").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.species", is("Saccharomyces cerevisiae")));
    }

    @Test
    @DisplayName("POST /api/fungi/identify : Doit identifier par profil mycologique")
    void shouldIdentifyByProfile() throws Exception {
        String json = "{\"type\":\"LEVURES\"}";

        mockMvc.perform(post("/api/fungi/identify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
            .with(auth())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[*].species", hasItem("Saccharomyces cerevisiae")));
    }

    @Test
    @DisplayName("Erreur 404 : Doit gérer les IDs inexistants")
    void shouldReturn404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/fungi/99999").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", is("Ressource Introuvable")))
            .andExpect(jsonPath("$.message", containsString("Champignon introuvable")));
    }

    @Test
    @DisplayName("GET /api/fungi/search sans query : Doit retourner 400 Bad Request")
    void shouldReturnBadRequestWhenMissingQuery() throws Exception {
        mockMvc.perform(get("/api/fungi/search").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/fungi/identify/api/{code} : 404 si code inconnu")
    void shouldReturn404ForUnknownApiCode() throws Exception {
        mockMvc.perform(get("/api/fungi/identify/api/UNKNOWN000").with(auth()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", is("Ressource Introuvable")))
            .andExpect(jsonPath("$.message", containsString("ne correspond au code API")));
    }
}
