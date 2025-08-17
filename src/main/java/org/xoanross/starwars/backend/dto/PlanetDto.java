package org.xoanross.starwars.backend.dto;

import java.time.OffsetDateTime;

public record PlanetDto(Long id, String name, OffsetDateTime created) {
}
