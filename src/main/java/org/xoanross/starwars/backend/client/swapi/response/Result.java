package org.xoanross.starwars.backend.client.swapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Result(Long uid, Properties properties) {
}
