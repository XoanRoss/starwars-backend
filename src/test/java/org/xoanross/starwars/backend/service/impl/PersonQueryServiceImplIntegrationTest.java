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
import org.xoanross.starwars.backend.dto.FilterDto;
import org.xoanross.starwars.backend.dto.PersonDto;
import org.xoanross.starwars.backend.service.PersonQueryService;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@SpringBootTest
class PersonQueryServiceImplIntegrationTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PersonQueryService personQueryService;

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
    @DisplayName("When get first page Then returns items sorted by id asc")
    void whenGetFirstPage_thenReturnsIdAsc() {
        var page = personQueryService.getPage(1, "id", "asc", null);
        assertThat(page.getContent()).extracting(PersonDto::id).containsExactly(1L, 2L, 3L);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(15);
    }

    @Test
    @DisplayName("When sort by name desc Then returns items name desc")
    void whenSortByNameDesc_thenReturnsNameDesc() {
        var page = personQueryService.getPage(1, "name", "desc", null);
        assertThat(page.getContent()).extracting(PersonDto::name).containsExactly("R2-D2", "Luke Skywalker", "C-3PO");
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(15);
    }

    @Test
    @DisplayName("When sort by created desc Then returns items created desc")
    void whenSortByCreatedDesc_thenReturnsCreatedDesc() {
        var page = personQueryService.getPage(1, "created", "desc", null);
        assertThat(page.getContent())
                .extracting(PersonDto::created)
                .containsExactly(OffsetDateTime.parse("2025-08-20T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-19T03:38:09.307Z"),
                        OffsetDateTime.parse("2025-08-18T03:38:09.307Z"));
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(15);
    }

    @Test
    @DisplayName("When filter by substring sky Then returns filtered asc")
    void whenFilterBySubstring_thenReturnsFilteredAsc() {
        var filter = new FilterDto("sky", null, null);
        var page = personQueryService.getPage(1, "id", "asc", filter);
        assertThat(page.getContent()).extracting(PersonDto::name).containsExactly("Luke Skywalker");
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(15);
    }

    @Test
    @DisplayName("When request page beyond data Then returns empty content")
    void whenRequestPageBeyondData_thenReturnsEmptyContent() {
        var page = personQueryService.getPage(2, "id", "asc", null);
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getSize()).isEqualTo(15);
    }

    @Test
    @DisplayName("When filter by name and date range Then returns intersection")
    void whenFilterByNameAndDateRange_thenReturnsIntersection() {
        var from = OffsetDateTime.parse("2025-08-18T00:00:00Z");
        var to = OffsetDateTime.parse("2025-08-19T23:59:59Z");
        var filter = new FilterDto("sky", from, to);
        var page = personQueryService.getPage(1, "id", "asc", filter);
        assertThat(page.getContent()).extracting(PersonDto::name).containsExactly("Luke Skywalker");
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("When filter by date range only Then returns matching dates")
    void whenFilterByDateRangeOnly_thenReturnsMatches() {
        var from = OffsetDateTime.parse("2025-08-19T00:00:00Z");
        var to = OffsetDateTime.parse("2025-08-20T23:59:59Z");
        var filter = new FilterDto(null, from, to);
        var page = personQueryService.getPage(1, "created", "asc", filter);
        assertThat(page.getContent()).extracting(PersonDto::name).containsExactly("C-3PO", "R2-D2");
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("When underlying data empty Then returns empty page")
    void whenUnderlyingDataEmpty_thenReturnsEmptyPage() {
        stubFor(get(urlMatching("/people.*")).atPriority(1).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"message\":\"ok\",\"total_records\":0,\"total_pages\":0,\"results\":[]}")));
        var page = personQueryService.getPage(1, "id", "asc", null);
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isZero();
        assertThat(page.getNumber()).isZero();
        assertThat(page.getSize()).isEqualTo(0);
    }

    @Test
    @DisplayName("When requesting second page with > pageSize items Then returns correct slice")
    void whenSecondPageWithManyItems_thenReturnsSlice() {
        String results = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> String.format("{\n  \"properties\": {\n    \"created\": \"2025-08-%02dT00:00:00Z\",\n    \"name\": \"Person %02d\"\n  },\n  \"uid\": \"%d\"\n}", 17 + (i % 10), i, i))
                .collect(Collectors.joining(",\n"));
        stubFor(get(urlMatching("/people.*")).atPriority(1).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{" +
                        "\"message\":\"ok\",\"total_records\":20,\"total_pages\":2,\"results\":[" + results + "]}")));
        var page = personQueryService.getPage(2, "id", "asc", null);
        assertThat(page.getContent()).extracting(PersonDto::id).containsExactly(16L, 17L, 18L, 19L, 20L);
        assertThat(page.getTotalElements()).isEqualTo(20);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getSize()).isEqualTo(15);
    }
}
