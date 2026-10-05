package com.intecx.inscope.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface EntityMapper<E, RQ, RS> extends BaseMapper<E, RS> {

    E toEntity(RQ request);

    default List<E> toEntityList(Collection<RQ> requests) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream().map(this::toEntity).toList();
    }
}
