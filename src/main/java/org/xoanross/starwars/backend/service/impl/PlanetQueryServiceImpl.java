package org.xoanross.starwars.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xoanross.starwars.backend.dto.PlanetDto;
import org.xoanross.starwars.backend.service.PlanetCacheService;
import org.xoanross.starwars.backend.service.PlanetFilterService;
import org.xoanross.starwars.backend.service.PlanetQueryService;
import org.xoanross.starwars.backend.service.PlanetSortService;

@Service
public class PlanetQueryServiceImpl extends AbstractQueryServiceImpl<PlanetDto> implements PlanetQueryService {

    public PlanetQueryServiceImpl(PlanetCacheService cacheService,
                                  PlanetSortService sortService,
                                  PlanetFilterService filterService,
                                  @Value("${settings.page-size}") int pageSize) {
        super(cacheService, sortService, filterService, pageSize);
    }

    @Override
    protected String getEntityName() {
        return "planets";
    }
}
