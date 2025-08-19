package org.xoanross.starwars.backend.service;

import org.springframework.data.domain.Page;
import org.xoanross.starwars.backend.dto.FilterDto;

public interface QueryService<T> {
    Page<T> getPage(int page, String sortBy, String sortDir, FilterDto filterDto);
}
