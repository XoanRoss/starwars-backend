package org.xoanross.starwars.backend.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.xoanross.starwars.backend.dto.PlanetDto;
import org.xoanross.starwars.backend.service.PlanetSortService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PlanetSortServiceImplIntegrationTest {

    @Autowired
    private PlanetSortService sortService;

    private final List<PlanetDto> DATA = List.of(
            new PlanetDto(1L, "Tatooine", OffsetDateTime.parse("2025-08-18T10:00:00Z")),
            new PlanetDto(2L, "Alderaan", OffsetDateTime.parse("2025-08-19T03:38:09.307Z")),
            new PlanetDto(3L, "Yavin IV", OffsetDateTime.parse("2025-08-20T12:15:30Z"))
    );

    @Test
    @DisplayName("When sort by id asc Then ordered by id asc")
    void whenSortByIdAsc_thenOrderedAsc() {
        var sorted = sortService.sort(DATA, "id", "asc");
        assertThat(sorted).extracting(PlanetDto::id).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("When sort by id desc Then ordered by id desc")
    void whenSortByIdDesc_thenOrderedDesc() {
        var sorted = sortService.sort(DATA, "id", "desc");
        assertThat(sorted).extracting(PlanetDto::id).containsExactly(3L, 2L, 1L);
    }

    @Test
    @DisplayName("When sort by name asc Then ordered case-insensitive")
    void whenSortByNameAsc_thenOrderedCaseInsensitive() {
        var sorted = sortService.sort(DATA, "name", "asc");
        assertThat(sorted).extracting(PlanetDto::name).containsExactly("Alderaan", "Tatooine", "Yavin IV");
    }

    @Test
    @DisplayName("When sort by name desc Then ordered desc case-insensitive")
    void whenSortByNameDesc_thenOrderedDescCaseInsensitive() {
        var sorted = sortService.sort(DATA, "name", "desc");
        assertThat(sorted).extracting(PlanetDto::name).containsExactly("Yavin IV", "Tatooine", "Alderaan");
    }

    @Test
    @DisplayName("When sort by created asc Then nulls last")
    void whenSortByCreatedAsc_thenNullsLast() {
        var sorted = sortService.sort(DATA, "created", "asc");
        assertThat(sorted).extracting(PlanetDto::id).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("When sort by created desc Then nulls first")
    void whenSortByCreatedDesc_thenNullsFirst() {
        var sorted = sortService.sort(DATA, "created", "desc");
        assertThat(sorted.get(0).id()).isEqualTo(3L);
        assertThat(sorted.subList(1, 3)).extracting(PlanetDto::id).containsExactly(2L, 1L);
    }

    @Test
    @DisplayName("When sort by unknown field Then returns list same size")
    void whenSortByUnknown_thenNoLoss() {
        var sorted = sortService.sort(DATA, "unknown", "asc");
        assertThat(sorted).hasSize(DATA.size());
    }
}
