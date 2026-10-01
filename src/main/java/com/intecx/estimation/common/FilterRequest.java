package com.intecx.estimation.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Schema(description = "Filtro de listado. Un filtro sin valores se ignora.")
public record FilterRequest(
        @Schema(example = "search", description = "Campo a filtrar (cada recurso define los suyos)")
        @NotBlank String key,

        @Schema(example = "LK")
        @NotNull FilterOperator operator,

        @Schema(example = "[\"ana\"]", description = "Nunca null; [] si no aplica")
        List<String> values
) {

    public FilterRequest {
        key = key == null ? "" : key.strip();
        values = values == null
                ? List.of()
                : values.stream()
                        .filter(Objects::nonNull)
                        .map(String::strip)
                        .filter(value -> !value.isEmpty())
                        .toList();
    }

    public boolean hasValues() {
        return !values.isEmpty();
    }
}
