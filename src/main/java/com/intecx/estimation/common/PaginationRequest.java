package com.intecx.estimation.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Paginación base 0. Valores fuera de rango se corrigen, no fallan.")
public record PaginationRequest(
        @Schema(example = "createdAt", description = "Campo de orden; si falta se usa el orden por defecto del recurso")
        String orderBy,

        @Schema(example = "20", description = "20 por defecto; <= 0 equivale a 20; máximo 100")
        int pageSize,

        @Schema(example = "0", description = "Base 0; negativo equivale a 0; si se pasa del final devuelve la última página")
        int pageNumber,

        @Schema(example = "DESC", description = "DESC por defecto")
        SortDirection sortDirection
) {

    public PaginationRequest {
        orderBy = orderBy == null ? "" : orderBy.strip();
        pageSize = pageSize <= 0
                ? PaginationConstants.DEFAULT_PAGE_SIZE
                : Math.min(pageSize, PaginationConstants.MAX_PAGE_SIZE);
        pageNumber = Math.max(pageNumber, PaginationConstants.FIRST_PAGE);
        sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
    }

    public static PaginationRequest defaults() {
        return new PaginationRequest("", PaginationConstants.DEFAULT_PAGE_SIZE, PaginationConstants.FIRST_PAGE, null);
    }
}
