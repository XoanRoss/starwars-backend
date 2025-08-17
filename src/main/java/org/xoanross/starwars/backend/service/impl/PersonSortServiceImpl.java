package org.xoanross.starwars.backend.service.impl;

import org.springframework.stereotype.Service;
import org.xoanross.starwars.backend.dto.PersonDto;
import org.xoanross.starwars.backend.service.PersonSortService;

import java.util.Comparator;
import java.util.Map;

@Service
public class PersonSortServiceImpl extends AbstractSortServiceImpl<PersonDto> implements PersonSortService {

    public PersonSortServiceImpl() {
        super(Map.of(
                "id", Comparator.comparing(PersonDto::id),
                "name", Comparator.comparing(PersonDto::name, String.CASE_INSENSITIVE_ORDER),
                "created", Comparator.comparing(PersonDto::created, Comparator.nullsLast(Comparator.naturalOrder())))
        );
    }

    @Override
    protected String getEntityName() {
        return "people";
    }
}
