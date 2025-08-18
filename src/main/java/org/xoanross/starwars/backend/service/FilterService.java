package org.xoanross.starwars.backend.service;

import org.xoanross.starwars.backend.dto.FilterDto;

import java.util.List;

public interface FilterService<T> {
    List<T> filter(List<T> items, FilterDto filterDto);
}
