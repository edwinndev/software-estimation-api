package com.intecx.inscope.common;

import com.intecx.inscope.common.FilterValues.Kind;
import com.intecx.inscope.common.FilterValues.Range;
import com.intecx.inscope.exception.BadRequestException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Locale;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ClassUtils;

@UtilityClass
class FilterSpecifications {

    private static final char LIKE_ESCAPE = '\\';

    <E> Specification<E> from(List<FilterRequest> filters, QueryFields fields) {
        List<Specification<E>> specifications = filters.stream()
                .filter(FilterRequest::hasValues)
                .<Specification<E>>map(filter -> specificationFor(filter, fields.attributePaths(filter.key())))
                .toList();
        return Specification.allOf(specifications);
    }

    private <E> Specification<E> specificationFor(FilterRequest filter, List<String> attributePaths) {
        return (root, query, cb) -> {
            Predicate[] predicates = attributePaths.stream()
                    .map(attributePath -> predicate(cb, pathOf(root, attributePath), filter))
                    .toArray(Predicate[]::new);
            return predicates.length == 1 ? predicates[0] : cb.or(predicates);
        };
    }

    private Path<?> pathOf(Root<?> root, String attributePath) {
        Path<?> path = root;
        for (String attribute : attributePath.split("\\.")) {
            path = path.get(attribute);
        }
        return path;
    }

    private Predicate predicate(CriteriaBuilder cb, Path<?> path, FilterRequest filter) {
        Class<?> type = ClassUtils.resolvePrimitiveIfNecessary(path.getJavaType());
        Kind kind = FilterValues.kindOf(type, filter.key());
        kind.requireSupported(filter.operator(), filter.key());
        return kind == Kind.TEXT
                ? textPredicate(cb, asText(path), filter)
                : typedPredicate(cb, path, type, filter);
    }

    private Predicate textPredicate(CriteriaBuilder cb, Expression<String> column, FilterRequest filter) {
        Expression<String> lowered = cb.lower(column);
        List<String> values = filter.values().stream().map(value -> value.toLowerCase(Locale.ROOT)).toList();
        String first = values.getFirst();
        return switch (filter.operator()) {
            case EQ -> cb.equal(lowered, first);
            case NE -> cb.notEqual(lowered, first);
            case LK -> cb.like(lowered, "%" + escapeLike(first) + "%", LIKE_ESCAPE);
            case IN -> lowered.in(values);
            default -> throw new IllegalStateException("Operador no soportado para texto: " + filter.operator());
        };
    }

    private Predicate typedPredicate(CriteriaBuilder cb, Path<?> column, Class<?> type, FilterRequest filter) {
        List<Range> ranges = filter.values().stream()
                .map(value -> FilterValues.parse(type, value, filter.key()))
                .toList();
        Range first = ranges.getFirst();
        return switch (filter.operator()) {
            case EQ -> equalTo(cb, column, first);
            case NE -> cb.not(equalTo(cb, column, first));
            case IN -> oneOf(cb, column, ranges);
            case GT -> compare(cb, column, FilterOperator.GT, first.low(), first.high());
            case GE -> compare(cb, column, FilterOperator.GE, first.low(), first.high());
            case LT -> compare(cb, column, FilterOperator.LT, first.low(), first.high());
            case LE -> compare(cb, column, FilterOperator.LE, first.low(), first.high());
            case BT -> {
                if (ranges.size() < 2) {
                    throw new BadRequestException("El operador BT del filtro '%s' requiere dos valores".formatted(filter.key()));
                }
                yield compare(cb, column, FilterOperator.BT, first.low(), ranges.get(1).high());
            }
            case LK -> throw new IllegalStateException("LK solo aplica a texto");
        };
    }

    private Predicate equalTo(CriteriaBuilder cb, Path<?> column, Range range) {
        return range.isPoint()
                ? cb.equal(column, range.low())
                : compare(cb, column, FilterOperator.BT, range.low(), range.high());
    }

    private Predicate oneOf(CriteriaBuilder cb, Path<?> column, List<Range> ranges) {
        if (ranges.stream().allMatch(Range::isPoint)) {
            return column.in(ranges.stream().map(Range::low).toList());
        }
        return cb.or(ranges.stream().map(range -> equalTo(cb, column, range)).toArray(Predicate[]::new));
    }

    @SuppressWarnings("unchecked")
    private <C extends Comparable<? super C>> Predicate compare(
            CriteriaBuilder cb, Path<?> path, FilterOperator operator, Object low, Object high) {
        Expression<C> column = (Expression<C>) path;
        C lower = (C) low;
        C upper = (C) high;
        return switch (operator) {
            case GT -> cb.greaterThan(column, upper);
            case GE -> cb.greaterThanOrEqualTo(column, lower);
            case LT -> cb.lessThan(column, lower);
            case LE -> cb.lessThanOrEqualTo(column, upper);
            case BT -> cb.between(column, lower, upper);
            default -> throw new IllegalStateException("Operador no soportado para comparar: " + operator);
        };
    }

    @SuppressWarnings("unchecked")
    private Expression<String> asText(Path<?> path) {
        return (Expression<String>) path;
    }

    private String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
