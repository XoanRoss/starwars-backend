package org.xoanross.starwars.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xoanross.starwars.backend.dto.FilterDto;
import org.xoanross.starwars.backend.dto.PersonDto;
import org.xoanross.starwars.backend.service.PersonFilterService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonFilterServiceImpl extends AbstractFilterServiceImpl<PersonDto> implements PersonFilterService {

    @Override
    protected String getEntityName() {
        return "people";
    }

    @Override
    protected String getNameField(PersonDto dto) {
        return dto.name();
    }

    @Override
    protected OffsetDateTime getCreatedField(PersonDto dto) {
        return dto.created();
    }

    @Override
    protected List<Predicate<PersonDto>> getCustomPredicates(FilterDto filterDto) {
        return List.of();
    }
}
