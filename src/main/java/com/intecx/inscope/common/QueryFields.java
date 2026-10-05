package com.intecx.inscope.common;

import com.intecx.inscope.exception.BadRequestException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryFields {

    private static final String TIE_BREAKER = "id";

    private final String defaultSort;
    private final Map<String, List<String>> filters = new LinkedHashMap<>();
    private final Set<String> sortable = new LinkedHashSet<>();

    public static QueryFields sortingBy(String defaultSort) {
        QueryFields fields = new QueryFields(defaultSort);
        fields.sortable.add(defaultSort);
        return fields;
    }

    public QueryFields filter(String key, String... attributePaths) {
        filters.put(key, List.of(attributePaths));
        return this;
    }

    public QueryFields sortable(String... attributes) {
        sortable.addAll(List.of(attributes));
        return this;
    }

    List<String> attributePaths(String key) {
        List<String> paths = filters.get(key);
        if (paths == null) {
            throw new BadRequestException(
                    "No se puede filtrar por '%s'. Campos permitidos: %s".formatted(key, String.join(", ", filters.keySet())));
        }
        return paths;
    }

    Sort sort(PaginationRequest pagination) {
        String attribute = pagination.orderBy().isEmpty() ? defaultSort : pagination.orderBy();
        if (!sortable.contains(attribute)) {
            throw new BadRequestException(
                    "No se puede ordenar por '%s'. Campos permitidos: %s".formatted(attribute, String.join(", ", sortable)));
        }
        Sort.Direction direction = pagination.sortDirection().toSpring();
        Sort sort = Sort.by(direction, attribute);
        return TIE_BREAKER.equals(attribute) ? sort : sort.and(Sort.by(direction, TIE_BREAKER));
    }
}
