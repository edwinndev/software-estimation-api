package com.intecx.inscope.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface BaseMapper<E, R> {

    R toResponse(E entity);

    default List<R> toResponseList(Collection<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toResponse).toList();
    }
}
