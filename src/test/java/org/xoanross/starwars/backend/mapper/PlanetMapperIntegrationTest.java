package org.xoanross.starwars.backend.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.xoanross.starwars.backend.client.swapi.response.Properties;
import org.xoanross.starwars.backend.client.swapi.response.Result;
import org.xoanross.starwars.backend.dto.PlanetDto;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PlanetMapperIntegrationTest {

    @Autowired
    private PlanetMapper planetMapper;

    @Test
    @DisplayName("When mapping single planet Result Then maps all fields")
    void whenMapSingle_thenMapsFields() {
        OffsetDateTime created = OffsetDateTime.parse("2025-08-18T10:00:00Z");
        Result result = new Result(1L, new Properties("Tatooine", created));

        PlanetDto dto = planetMapper.toPlanet(result);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Tatooine");
        assertThat(dto.created()).isEqualTo(created);
    }

    @Test
    @DisplayName("When mapping planet list Then preserves order and null created")
    void whenMapList_thenPreservesOrderAndNull() {
        Result r1 = new Result(2L, new Properties("Alderaan", OffsetDateTime.parse("2025-08-19T03:38:09.307Z")));
        Result r2 = new Result(3L, new Properties("Yavin IV", null));

        List<PlanetDto> list = planetMapper.toPlanetList(List.of(r1, r2));

        assertThat(list).hasSize(2);
        assertThat(list.get(0).id()).isEqualTo(2L);
        assertThat(list.get(0).name()).isEqualTo("Alderaan");
        assertThat(list.get(1).id()).isEqualTo(3L);
        assertThat(list.get(1).name()).isEqualTo("Yavin IV");
        assertThat(list.get(1).created()).isNull();
    }
}
