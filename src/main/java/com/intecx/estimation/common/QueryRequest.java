package com.intecx.estimation.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;

@Schema(description = "Cuerpo de todos los POST /search: filtros + paginación")
public record QueryRequest(
        @Valid List<FilterRequest> filters,
        @Valid PaginationRequest pagination
) {

    public QueryRequest {
        filters = filters == null ? List.of() : filters;
        pagination = pagination == null ? PaginationRequest.defaults() : pagination;
    }
}
