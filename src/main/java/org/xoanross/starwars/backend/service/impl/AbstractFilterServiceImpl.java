package org.xoanross.starwars.backend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.xoanross.starwars.backend.dto.FilterDto;
import org.xoanross.starwars.backend.service.FilterService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public abstract class AbstractFilterServiceImpl<T> implements FilterService<T> {

    @Override
    public List<T> filter(List<T> items, FilterDto filter) {
        if (filter == null) return items;

        log.info("Filtering {} by {}", getEntityName(), filter);

        List<Predicate<T>> predicates = getCommonPredicates(filter);
        List<Predicate<T>> customPredicates = getCustomPredicates(filter);
        predicates.addAll(customPredicates);

        Predicate<T> combinedPredicates = predicates.stream().reduce(x -> true, Predicate::and);
        List<T> filteredItems = items.stream()
                .filter(combinedPredicates)
                .toList();

        log.info("Found {} {} after filtering", filteredItems.size(), getEntityName());
        return filteredItems;
    }

    private List<Predicate<T>> getCommonPredicates(FilterDto filterDto) {
        List<Predicate<T>> commonFilters = new ArrayList<>();
        commonFilters.add(item -> nameFilter(item, filterDto.name()));
        commonFilters.add(item -> createdFilter(item, filterDto.createdFrom(), filterDto.createdTo()));
        return commonFilters;
    }

    private boolean nameFilter(T item, String filterName) {
        String itemName = getNameField(item);
        return StringUtils.isBlank(filterName) || (itemName != null && StringUtils.containsIgnoreCase(itemName, filterName));
    }

    private boolean createdFilter(T item, OffsetDateTime from, OffsetDateTime to) {
        if (from == null && to == null) return true;

        OffsetDateTime itemCreatedDate = getCreatedField(item);
        if (itemCreatedDate == null) return false;

        return (from == null || !itemCreatedDate.isBefore(from))
                && (to == null || !itemCreatedDate.isAfter(to));
    }

    protected abstract String getEntityName();

    protected abstract String getNameField(T item);

    protected abstract OffsetDateTime getCreatedField(T item);

    protected abstract List<Predicate<T>> getCustomPredicates(FilterDto filterDto);
}
