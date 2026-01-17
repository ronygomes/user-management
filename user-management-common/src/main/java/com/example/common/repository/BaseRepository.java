package com.example.common.repository;

import java.util.Optional;

public interface BaseRepository<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
}
