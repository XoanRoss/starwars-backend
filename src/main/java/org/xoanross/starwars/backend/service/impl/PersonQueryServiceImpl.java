package org.xoanross.starwars.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xoanross.starwars.backend.dto.PersonDto;
import org.xoanross.starwars.backend.service.PersonCacheService;
import org.xoanross.starwars.backend.service.PersonFilterService;
import org.xoanross.starwars.backend.service.PersonQueryService;
import org.xoanross.starwars.backend.service.PersonSortService;

@Service
public class PersonQueryServiceImpl extends AbstractQueryServiceImpl<PersonDto> implements PersonQueryService {

    public PersonQueryServiceImpl(PersonCacheService cacheService,
                                  PersonSortService sortService,
                                  PersonFilterService filterService,
                                  @Value("${settings.page-size}") int pageSize) {
        super(cacheService, sortService, filterService, pageSize);
    }

    @Override
    protected String getEntityName() {
        return "people";
    }
}
