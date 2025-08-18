package org.xoanross.starwars.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xoanross.starwars.backend.dto.FilterDto;
import org.xoanross.starwars.backend.dto.PlanetDto;
import org.xoanross.starwars.backend.service.PlanetFilterService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlanetFilterServiceImpl extends AbstractFilterServiceImpl<PlanetDto> implements PlanetFilterService {

    @Override
    protected String getEntityName() {
        return "planets";
    }

    @Override
    protected String getNameField(PlanetDto dto) {
        return dto.name();
    }

    @Override
    protected OffsetDateTime getCreatedField(PlanetDto dto) {
        return dto.created();
    }

    @Override
    protected List<Predicate<PlanetDto>> getCustomPredicates(FilterDto filterDto) {
        return List.of();
    }
}
