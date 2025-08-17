package org.xoanross.starwars.backend.dto;

import java.time.OffsetDateTime;

public record PersonDto(Long id, String name, OffsetDateTime created) {
}
