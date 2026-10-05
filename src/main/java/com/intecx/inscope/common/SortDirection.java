package com.intecx.inscope.common;

import org.springframework.data.domain.Sort;

public enum SortDirection {
    ASC,
    DESC;

    public Sort.Direction toSpring() {
        return Sort.Direction.valueOf(name());
    }
}
