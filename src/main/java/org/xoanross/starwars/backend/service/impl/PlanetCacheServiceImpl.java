package org.xoanross.starwars.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.xoanross.starwars.backend.client.swapi.SwapiClient;
import org.xoanross.starwars.backend.client.swapi.response.PagedResponse;
import org.xoanross.starwars.backend.dto.PlanetDto;
import org.xoanross.starwars.backend.mapper.PlanetMapper;
import org.xoanross.starwars.backend.service.PlanetCacheService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlanetCacheServiceImpl implements PlanetCacheService {

    private final SwapiClient swapiClient;
    private final PlanetMapper planetMapper;

    @Cacheable("planets")
    public List<PlanetDto> getAllCached() {
        log.info("Fetching all planets from SWAPI (cacheable)");

        PagedResponse response = swapiClient.getPlanets();
        List<PlanetDto> planets = planetMapper.toPlanetList(response.results());

        log.info("Fetched {} planets from SWAPI, caching result", planets.size());
        return planets;
    }
}
