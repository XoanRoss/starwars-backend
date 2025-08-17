package org.xoanross.starwars.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xoanross.starwars.backend.service.SortService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSortServiceImpl<T> implements SortService<T> {

    private final Map<String, Comparator<T>> comparators;

    @Override
    public List<T> sort(List<T> items, String sortBy, String sortDir) {
        log.info("Sorting {} by {} in {} order", getEntityName(), sortBy, sortDir);

        Comparator<T> comparator = comparators.getOrDefault(sortBy, Comparator.comparingInt(Object::hashCode));
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        List<T> sortedItems = items.stream().sorted(comparator).toList();

        log.info("Sorted {} by {} in {} order, found {} items", getEntityName(), sortBy, sortDir, sortedItems.size());
        return sortedItems;
    }

    protected abstract String getEntityName();
}
