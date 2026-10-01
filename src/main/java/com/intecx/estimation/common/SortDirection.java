package com.intecx.estimation.common;

import org.springframework.data.domain.Sort;

public enum SortDirection {
    ASC,
    DESC;

    public Sort.Direction toSpring() {
        return Sort.Direction.valueOf(name());
    }
}
