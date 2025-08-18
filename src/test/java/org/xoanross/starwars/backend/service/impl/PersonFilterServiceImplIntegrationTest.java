package org.xoanross.starwars.backend.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.xoanross.starwars.backend.dto.FilterDto;
import org.xoanross.starwars.backend.dto.PersonDto;
import org.xoanross.starwars.backend.service.PersonFilterService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PersonFilterServiceImplIntegrationTest {

    @Autowired
    private PersonFilterService filterService;

    private final List<PersonDto> DATA = List.of(
            new PersonDto(1L, "Luke Skywalker", OffsetDateTime.parse("2025-08-18T10:00:00Z")),
            new PersonDto(2L, "C-3PO", OffsetDateTime.parse("2025-08-19T03:38:09.307Z")),
            new PersonDto(3L, "R2-D2", OffsetDateTime.parse("2025-08-20T12:15:30Z"))
    );

    @Test
    @DisplayName("When filter is null Then returns original list")
    void whenFilterIsNull_thenReturnsOriginalList() {
        var result = filterService.filter(DATA, null);
        assertThat(result).containsExactlyElementsOf(DATA);
    }

    @Test
    @DisplayName("When filter name substring Then returns matches")
    void whenFilterNameSubstring_thenReturnsMatches() {
        var result = filterService.filter(DATA, new FilterDto("sky", null, null));
        assertThat(result).extracting(PersonDto::name).containsExactly("Luke Skywalker");
    }

    @Test
    @DisplayName("When filter from exact timestamp Then includes exact and after")
    void whenFilterFromExactTimestamp_thenIncludesExactAndAfter() {
        var from = OffsetDateTime.parse("2025-08-19T03:38:09.307Z");
        var result = filterService.filter(DATA, new FilterDto(null, from, null));
        assertThat(result).extracting(PersonDto::id).containsExactly(2L, 3L);
    }

    @Test
    @DisplayName("When filter from just after timestamp Then excludes previous")
    void whenFilterFromAfterTimestamp_thenExcludesPrevious() {
        var from = OffsetDateTime.parse("2025-08-19T03:38:09.308Z");
        var result = filterService.filter(DATA, new FilterDto(null, from, null));
        assertThat(result).extracting(PersonDto::id).containsExactly(3L);
    }

    @Test
    @DisplayName("When filter to exact timestamp Then includes before and exact")
    void whenFilterToExactTimestamp_thenIncludesBeforeAndExact() {
        var to = OffsetDateTime.parse("2025-08-19T03:38:09.307Z");
        var result = filterService.filter(DATA, new FilterDto(null, null, to));
        assertThat(result).extracting(PersonDto::id).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("When filter range Then returns inclusive matches")
    void whenFilterRange_thenReturnsInclusiveMatches() {
        var from = OffsetDateTime.parse("2025-08-19T00:00:00Z");
        var to = OffsetDateTime.parse("2025-08-20T12:15:30Z");
        var result = filterService.filter(DATA, new FilterDto(null, from, to));
        assertThat(result).extracting(PersonDto::id).containsExactly(2L, 3L);
    }

    @Test
    @DisplayName("When filter has no matches Then returns empty list")
    void whenFilterNoMatches_thenReturnsEmpty() {
        var result = filterService.filter(DATA, new FilterDto("zzz", null, null));
        assertThat(result).isEmpty();
    }
}
