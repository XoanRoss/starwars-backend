package org.xoanross.starwars.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.xoanross.starwars.backend.client.swapi.response.Result;
import org.xoanross.starwars.backend.dto.PersonDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(target = "id", source = "uid")
    @Mapping(target = "name", source = "properties.name")
    @Mapping(target = "created", source = "properties.created")
    PersonDto toPerson(Result result);

    List<PersonDto> toPersonList(List<Result> results);
}
