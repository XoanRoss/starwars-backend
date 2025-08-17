package org.xoanross.starwars.backend.service.impl;

import org.springframework.stereotype.Service;
import org.xoanross.starwars.backend.dto.PlanetDto;
import org.xoanross.starwars.backend.service.PlanetSortService;

import java.util.Comparator;
import java.util.Map;

@Service
public class PlanetSortServiceImpl extends AbstractSortServiceImpl<PlanetDto> implements PlanetSortService {

    public PlanetSortServiceImpl() {
        super(Map.of(
                "id", Comparator.comparing(PlanetDto::id),
                "name", Comparator.comparing(PlanetDto::name, String.CASE_INSENSITIVE_ORDER),
                "created", Comparator.comparing(PlanetDto::created, Comparator.nullsLast(Comparator.naturalOrder())))
        );
    }

    @Override
    protected String getEntityName() {
        return "planets";
    }
}
