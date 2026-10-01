package com.intecx.estimation.common;

import io.swagger.v3.oas.annotations.media.Schema;

public record PageMeta(
        @Schema(example = "20") int pageSize,
        @Schema(example = "0", description = "Página efectiva, base 0") int pageNumber,
        @Schema(example = "3", description = "0 cuando no hay elementos") int totalPages,
        @Schema(example = "52") long totalElements,
        @Schema(example = "true") boolean hasNext,
        @Schema(example = "false") boolean hasPrevious
) {

    public static PageMeta of(long totalElements, int pageNumber, int pageSize) {
        int totalPages = (int) ((totalElements + pageSize - 1) / pageSize);
        return new PageMeta(
                pageSize,
                pageNumber,
                totalPages,
                totalElements,
                pageNumber < totalPages - 1,
                pageNumber > 0
        );
    }
}
