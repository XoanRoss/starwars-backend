package org.xoanross.starwars.backend.service.impl;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.xoanross.starwars.backend.dto.PlanetDto;
import org.xoanross.starwars.backend.exception.model.ClientException;
import org.xoanross.starwars.backend.service.PlanetCacheService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class PlanetCacheServiceImplIntegrationTest {

    @Autowired
    private PlanetCacheService planetCacheService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(cacheManager.getCache("planets")).clear();
        WireMock.reset();

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
    @DisplayName("When calling for first time Then returns mapped list")
    void whenFirstCall_returnsMappedList() {
        List<PlanetDto> result = planetCacheService.getAllCached();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(PlanetDto::id).containsExactly(1L, 2L, 3L);
        assertThat(result).extracting(PlanetDto::name).containsExactly("Tatooine", "Alderaan", "Yavin IV");
        assertThat(result).extracting(PlanetDto::created)
                .containsExactly(OffsetDateTime.parse("2025-08-18T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-19T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-20T03:38:09.307Z"));
        verify(1, getRequestedFor(urlMatching("/planets.*")));
    }

    @Test
    @DisplayName("When calling for second time Then returns cached list")
    void whenSecondCall_usesCache() {
        List<PlanetDto> firstCall = planetCacheService.getAllCached();
        List<PlanetDto> secondCall = planetCacheService.getAllCached();

        assertThat(firstCall).hasSize(3);
        assertThat(firstCall).extracting(PlanetDto::id).containsExactly(1L, 2L, 3L);
        assertThat(firstCall).extracting(PlanetDto::name).containsExactly("Tatooine", "Alderaan", "Yavin IV");
        assertThat(firstCall).extracting(PlanetDto::created)
                .containsExactly(OffsetDateTime.parse("2025-08-18T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-19T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-20T03:38:09.307Z"));
        assertThat(secondCall).isSameAs(firstCall);
        verify(1, getRequestedFor(urlMatching("/planets.*")));
    }

    @Test
    @DisplayName("When client error Then return ClientException")
    void whenClientError_thenThrowsClientException() {
        stubFor(get(urlMatching("/planets.*")).atPriority(1).willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"Some error\"}")));

        assertThatThrownBy(() -> planetCacheService.getAllCached())
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("Error calling client");
    }

    @Test
    @DisplayName("When remote returns empty list Then returns empty and caches result")
    void whenEmptyList_returnsEmpty() {
        stubFor(get(urlMatching("/planets.*")).atPriority(1).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {"message":"ok","total_records":0,"total_pages":0,"results":[]}
                        """)));

        List<PlanetDto> first = planetCacheService.getAllCached();
        List<PlanetDto> second = planetCacheService.getAllCached();

        assertThat(first).isEmpty();
        assertThat(second).isSameAs(first);
        verify(1, getRequestedFor(urlMatching("/planets.*")));
    }
}
