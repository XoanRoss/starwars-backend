package org.xoanross.starwars.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PlanetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(cacheManager.getCache("planets")).clear();

        stubFor(get(urlMatching("/planets.*")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {
                          "message": "ok",
                          "total_records": 3,
                          "total_pages": 1,
                          "results": [
                            {
                              "properties": {
                                "created": "2025-08-18T03:38:09.307Z",
                                "name": "Tatooine"
                              },
                              "uid": "1"
                            },
                            {
                              "properties": {
                                "created": "2025-08-19T03:38:09.307Z",
                                "name": "Alderaan"
                              },
                              "uid": "2"
                            },
                            {
                              "properties": {
                                "created": "2025-08-20T03:38:09.307Z",
                                "name": "Yavin IV"
                              },
                              "uid": "3"
                            }
                          ]
                        }
                        """)));
    }

    @Test
    @DisplayName("When request first page Then returns id asc")
    void whenRequestFirstPage_thenReturnsIdAsc() throws Exception {
        mockMvc.perform(post("/planets")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[2].id").value(3))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("When sort by name desc Then returns list ordered desc")
    void whenSortByNameDesc_thenReturnsOrderedDesc() throws Exception {
        mockMvc.perform(post("/planets")
                        .param("page", "1")
                        .param("sortBy", "name")
                        .param("sortDir", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].name").value("Yavin IV"))
                .andExpect(jsonPath("$.content[1].name").value("Tatooine"))
                .andExpect(jsonPath("$.content[2].name").value("Alderaan"))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("When filter by substring der Then returns only matches sorted asc")
    void whenFilterBySubstring_thenReturnsMatchesAsc() throws Exception {
        String filter = "{\"name\":\"der\"}";
        mockMvc.perform(post("/planets")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Alderaan"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("When request page beyond data Then returns empty content")
    void whenRequestPageBeyondData_thenReturnsEmptyContent() throws Exception {
        mockMvc.perform(post("/planets")
                        .param("page", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.number").value(1));
    }
}
