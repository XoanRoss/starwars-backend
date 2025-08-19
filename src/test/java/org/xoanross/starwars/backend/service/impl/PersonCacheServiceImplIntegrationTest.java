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
import org.xoanross.starwars.backend.dto.PersonDto;
import org.xoanross.starwars.backend.exception.model.ClientException;
import org.xoanross.starwars.backend.service.PersonCacheService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class PersonCacheServiceImplIntegrationTest {

    @Autowired
    private PersonCacheService personCacheService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(cacheManager.getCache("people")).clear();
        WireMock.reset();

        stubFor(get(urlMatching("/people.*")).willReturn(aResponse()
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
                                "name": "Luke Skywalker"
                              },
                              "uid": "1"
                            },
                            {
                              "properties": {
                                "created": "2025-08-19T03:38:09.307Z",
                                "name": "C-3PO"
                              },
                              "uid": "2"
                            },
                            {
                              "properties": {
                                "created": "2025-08-20T03:38:09.307Z",
                                "name": "R2-D2"
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
        List<PersonDto> result = personCacheService.getAllCached();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(PersonDto::id).containsExactly(1L, 2L, 3L);
        assertThat(result).extracting(PersonDto::name).containsExactly("Luke Skywalker", "C-3PO", "R2-D2");
        assertThat(result).extracting(PersonDto::created)
                .containsExactly(OffsetDateTime.parse("2025-08-18T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-19T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-20T03:38:09.307Z"));
        verify(1, getRequestedFor(urlMatching("/people.*")));
    }

    @Test
    @DisplayName("When calling for second time Then returns cached list")
    void whenSecondCall_usesCache() {
        List<PersonDto> firstCall = personCacheService.getAllCached();
        List<PersonDto> secondCall = personCacheService.getAllCached();

        assertThat(firstCall).hasSize(3);
        assertThat(firstCall).extracting(PersonDto::id).containsExactly(1L, 2L, 3L);
        assertThat(firstCall).extracting(PersonDto::name).containsExactly("Luke Skywalker", "C-3PO", "R2-D2");
        assertThat(firstCall).extracting(PersonDto::created)
                .containsExactly(OffsetDateTime.parse("2025-08-18T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-19T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-20T03:38:09.307Z"));
        assertThat(secondCall).isSameAs(firstCall);
        verify(1, getRequestedFor(urlMatching("/people.*")));
    }

    @Test
    @DisplayName("When client error Then return ClientException")
    void whenClientError_thenThrowsClientException() {
        stubFor(get(urlMatching("/people.*")).atPriority(1).willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"Some error\"}")));

        assertThatThrownBy(() -> personCacheService.getAllCached())
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("Error calling client");
    }

    @Test
    @DisplayName("When remote returns empty list Then returns empty and caches result")
    void whenEmptyList_returnsEmpty() {
        stubFor(get(urlMatching("/people.*")).atPriority(1).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {"message":"ok","total_records":0,"total_pages":0,"results":[]}
                        """)));

        List<PersonDto> first = personCacheService.getAllCached();
        List<PersonDto> second = personCacheService.getAllCached();

        assertThat(first).isEmpty();
        assertThat(second).isSameAs(first);
        verify(1, getRequestedFor(urlMatching("/people.*")));
    }
}
