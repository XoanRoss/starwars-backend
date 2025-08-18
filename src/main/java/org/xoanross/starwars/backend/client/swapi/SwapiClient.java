package org.xoanross.starwars.backend.client.swapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.xoanross.starwars.backend.client.swapi.response.PagedResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwapiClient {

    private final WebClient webClient;

    public PagedResponse getPeople() {
        return getAll("/people");
    }

    public PagedResponse getPlanets() {
        return getAll("/planets");
    }

    private PagedResponse getAll(String resource) {
        log.info("Calling SWAPI Client - {}", resource);
        return webClient.get()
                .uri(buildUrl(resource))
                .retrieve()
                .bodyToMono(PagedResponse.class)
                .block();
    }

    private String buildUrl(String resource) {
        return UriComponentsBuilder.fromPath("/" + resource)
                .queryParam("expanded", true)
                .queryParam("page", 1)
                .queryParam("limit", 0)
                .toUriString();
    }
}
