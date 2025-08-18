package org.xoanross.starwars.backend.client.swapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Properties(String name, OffsetDateTime created) {
}
