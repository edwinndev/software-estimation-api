package com.intecx.inscope.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
@Schema(description = """
        Página de resultados. Las filas viajan bajo la clave del recurso \
        (userResponse, projectResponse, ...) junto a los metadatos de paginación.""")
public class PaginatedResponse<T> {

    @JsonIgnore
    private final String collectionKey;

    @JsonIgnore
    private final List<T> items;

    @JsonUnwrapped
    private final PageMeta meta;

    @JsonAnyGetter
    @Schema(hidden = true)
    public Map<String, List<T>> collection() {
        return Map.of(collectionKey, items);
    }
}
