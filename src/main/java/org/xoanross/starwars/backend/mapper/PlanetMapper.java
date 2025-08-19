package org.xoanross.starwars.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.xoanross.starwars.backend.client.swapi.response.Result;
import org.xoanross.starwars.backend.dto.PlanetDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanetMapper {

    @Mapping(target = "id", source = "uid")
    @Mapping(target = "name", source = "properties.name")
    @Mapping(target = "created", source = "properties.created")
    PlanetDto toPlanet(Result result);

    List<PlanetDto> toPlanetList(List<Result> results);
}
