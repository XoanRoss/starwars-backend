package org.xoanross.starwars.backend.client.swapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.xoanross.starwars.backend.client.swapi.response.PagedResponse;
import org.xoanross.starwars.backend.exception.model.ClientException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class SwapiClientIntegrationTest {

    @Autowired
    private SwapiClient swapiClient;

    @Test
    @DisplayName("getPeople devuelve respuesta paginada y mapeo correcto")
    void getPeople_ok() {
        stubFor(get(urlMatching("/people.*")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {
                          "message": "ok",
                          "total_records": 2,
                          "total_pages": 1,
                          "results": [
                            {
                              "properties": {"created": "2025-08-18T03:38:09.307Z", "name": "Luke"},
                              "uid": "1"
                            },
                            {
                              "properties": {"created": "2025-08-19T03:38:09.307Z", "name": "Leia"},
                              "uid": "2"
                            }
                          ]
                        }
                        """)));

        PagedResponse response = swapiClient.getPeople();

        assertThat(response.totalRecords()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.results()).hasSize(2);
        assertThat(response.results().getFirst().uid()).isEqualTo(1L);
        assertThat(response.results().getFirst().properties().name()).isEqualTo("Luke");
    }

    @Test
    @DisplayName("getPlanets devuelve respuesta paginada y mapeo correcto")
    void getPlanets_ok() {
        stubFor(get(urlMatching("/planets.*")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {
                          "message": "ok",
                          "total_records": 1,
                          "total_pages": 1,
                          "results": [
                            {
                              "properties": {"created": "2025-08-18T03:38:09.307Z", "name": "Tatooine"},
                              "uid": "10"
                            }
                          ]
                        }
                        """)));

        PagedResponse response = swapiClient.getPlanets();

        assertThat(response.totalRecords()).isEqualTo(1);
        assertThat(response.results()).hasSize(1);
        assertThat(response.results().getFirst().uid()).isEqualTo(10L);
        assertThat(response.results().getFirst().properties().name()).isEqualTo("Tatooine");
    }

    @Test
    @DisplayName("Error HTTP lanza ClientException")
    void getPeople_error() {
        stubFor(get(urlMatching("/people.*")).willReturn(aResponse()
                .withStatus(502)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"bad gateway\"}")));

        assertThatThrownBy(() -> swapiClient.getPeople())
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("502");
    }
}
