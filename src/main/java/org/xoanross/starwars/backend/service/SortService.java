package org.xoanross.starwars.backend.service;

import java.util.List;

public interface SortService<T> {
    List<T> sort(List<T> items, String sortBy, String sortDir);
}
