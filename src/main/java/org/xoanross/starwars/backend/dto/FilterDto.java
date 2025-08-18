package org.xoanross.starwars.backend.dto;

import java.time.OffsetDateTime;

public record FilterDto(String name, OffsetDateTime createdFrom, OffsetDateTime createdTo) {
}
