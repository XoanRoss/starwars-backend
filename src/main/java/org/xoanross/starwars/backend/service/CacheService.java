package org.xoanross.starwars.backend.service;

import java.util.List;

public interface CacheService<T> {
    List<T> getAllCached();
}
