package org.xoanross.starwars.backend.client.swapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PagedResponse(@JsonProperty("total_records") int totalRecords,
                            @JsonProperty("total_pages") int totalPages, List<Result> results) {
}
