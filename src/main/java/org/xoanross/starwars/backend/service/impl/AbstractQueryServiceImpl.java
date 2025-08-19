package org.xoanross.starwars.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.xoanross.starwars.backend.dto.FilterDto;
import org.xoanross.starwars.backend.service.CacheService;
import org.xoanross.starwars.backend.service.FilterService;
import org.xoanross.starwars.backend.service.QueryService;
import org.xoanross.starwars.backend.service.SortService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractQueryServiceImpl<T> implements QueryService<T> {

    private final CacheService<T> cacheService;
    private final SortService<T> sortService;
    private final FilterService<T> filterService;
    private final int pageSize;

    @Override
    public Page<T> getPage(int page, String sortBy, String sortDir, FilterDto filterDto) {
        log.info("Fetching {} page: {} with sort by: {} in: {} order and filter: {}", getEntityName(), page, sortBy, sortDir, filterDto);

        List<T> items = cacheService.getAllCached();
        if (items.isEmpty()) {
            log.warn("No {} found", getEntityName());
            return Page.empty();
        }

        items = filterService.filter(items, filterDto);
        items = sortService.sort(items, sortBy, sortDir);

        log.info("Fetched {} {} in page: {}", items.size(), getEntityName(), page);
        return createPage(items, page);
    }

    private Page<T> createPage(List<T> items, int page) {
        List<T> pageContent = getPageContent(items, page);
        return new PageImpl<>(pageContent, Pageable.ofSize(pageSize).withPage(page - 1), items.size());
    }

    private List<T> getPageContent(List<T> items, int page) {
        int fromIndex = Math.max(0, (page - 1) * pageSize);
        int toIndex = Math.min(fromIndex + pageSize, items.size());
        return fromIndex < toIndex ? items.subList(fromIndex, toIndex) : List.of();
    }

    protected abstract String getEntityName();
}
