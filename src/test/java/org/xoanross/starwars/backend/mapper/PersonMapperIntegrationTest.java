package org.xoanross.starwars.backend.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.xoanross.starwars.backend.client.swapi.response.Properties;
import org.xoanross.starwars.backend.client.swapi.response.Result;
import org.xoanross.starwars.backend.dto.PersonDto;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PersonMapperIntegrationTest {

    @Autowired
    private PersonMapper personMapper;

    @Test
    @DisplayName("When mapping single Result Then maps all fields")
    void whenMapSingle_thenMapsFields() {
        OffsetDateTime created = OffsetDateTime.parse("2025-08-18T10:00:00Z");
        Result result = new Result(1L, new Properties("Luke Skywalker", created));

        PersonDto dto = personMapper.toPerson(result);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Luke Skywalker");
        assertThat(dto.created()).isEqualTo(created);
    }

    @Test
    @DisplayName("When mapping list Then preserves order and null created")
    void whenMapList_thenPreservesOrderAndNull() {
        Result r1 = new Result(2L, new Properties("C-3PO", OffsetDateTime.parse("2025-08-19T03:38:09.307Z")));
        Result r2 = new Result(3L, new Properties("R2-D2", null));

        List<PersonDto> list = personMapper.toPersonList(List.of(r1, r2));

        assertThat(list).hasSize(2);
        assertThat(list.get(0).id()).isEqualTo(2L);
        assertThat(list.get(0).name()).isEqualTo("C-3PO");
        assertThat(list.get(1).id()).isEqualTo(3L);
        assertThat(list.get(1).name()).isEqualTo("R2-D2");
        assertThat(list.get(1).created()).isNull();
    }
}
