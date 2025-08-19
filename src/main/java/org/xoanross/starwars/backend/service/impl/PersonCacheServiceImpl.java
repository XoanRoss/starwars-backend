package org.xoanross.starwars.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.xoanross.starwars.backend.client.swapi.SwapiClient;
import org.xoanross.starwars.backend.client.swapi.response.PagedResponse;
import org.xoanross.starwars.backend.dto.PersonDto;
import org.xoanross.starwars.backend.mapper.PersonMapper;
import org.xoanross.starwars.backend.service.PersonCacheService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonCacheServiceImpl implements PersonCacheService {

    private final SwapiClient swapiClient;
    private final PersonMapper personMapper;

    @Cacheable("people")
    public List<PersonDto> getAllCached() {
        log.info("Fetching all people from SWAPI (cacheable)");

        PagedResponse response = swapiClient.getPeople();
        List<PersonDto> people = personMapper.toPersonList(response.results());

        log.info("Fetched {} people from SWAPI, caching result", people.size());
        return people;
    }
}
