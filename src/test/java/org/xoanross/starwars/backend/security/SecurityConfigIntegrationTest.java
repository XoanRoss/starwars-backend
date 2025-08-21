package org.xoanross.starwars.backend.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.xoanross.starwars.backend.service.PersonQueryService;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonQueryService personQueryService;

    @Test
    @DisplayName("When POST with allowed origin Then returns CORS allow origin header")
    void whenAllowedOrigin_thenCorsHeaderPresent() throws Exception {
        mockMvc.perform(post("/people")
                        .contentType(APPLICATION_JSON)
                        .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    @DisplayName("When POST with disallowed origin Then returns 403 and no allow origin header")
    void whenDisallowedOrigin_thenForbiddenAndNoCorsHeader() throws Exception {
        mockMvc.perform(post("/people")
                        .contentType(APPLICATION_JSON)
                        .header("Origin", "http://origin-not-valid.com"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"))
                .andExpect(content().string("Invalid CORS request"));
    }
}
