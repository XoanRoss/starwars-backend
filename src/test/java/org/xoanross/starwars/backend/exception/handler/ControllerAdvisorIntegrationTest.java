package org.xoanross.starwars.backend.exception.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.xoanross.starwars.backend.exception.model.ClientException;
import org.xoanross.starwars.backend.service.PersonQueryService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControllerAdvisorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonQueryService personQueryService;

    @Test
    @DisplayName("When service throws ClientException Then returns propagated code and message")
    void whenClientException_thenReturnsClientErrorPayload() throws Exception {
        when(personQueryService.getPage(anyInt(), anyString(), anyString(), any()))
                .thenThrow(new ClientException(400, "Client error"));

        mockMvc.perform(post("/people")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Client error"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("When service throws generic exception Then returns 500 generic message")
    void whenGenericException_thenReturnsGenericErrorPayload() throws Exception {
        when(personQueryService.getPage(anyInt(), anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/people")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("Unknown error occurred. Please contact support. Error ID: ")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
