package com.intecx.inscope.common;

import static java.util.Map.entry;

import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
class FilterValues {

    private static final int DATE_ONLY_LENGTH = 10;

    private static final Map<Class<?>, Function<String, Object>> PARSERS = Map.ofEntries(
            entry(Boolean.class, FilterValues::parseBoolean),
            entry(UUID.class, UUID::fromString),
            entry(Byte.class, Byte::valueOf),
            entry(Short.class, Short::valueOf),
            entry(Integer.class, Integer::valueOf),
            entry(Long.class, Long::valueOf),
            entry(Float.class, Float::valueOf),
            entry(Double.class, Double::valueOf),
            entry(BigInteger.class, BigInteger::new),
            entry(BigDecimal.class, BigDecimal::new),
            entry(LocalDate.class, raw -> LocalDate.parse(raw.substring(0, Math.min(raw.length(), DATE_ONLY_LENGTH))))
    );

    private static final Map<Class<?>, Function<Instant, Object>> TEMPORAL_CONVERTERS = Map.of(
            Instant.class, instant -> instant,
            OffsetDateTime.class, instant -> instant.atOffset(ZoneOffset.UTC),
            LocalDateTime.class, instant -> LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    );

    enum Kind {
        TEXT(FilterOperator.EQ, FilterOperator.NE, FilterOperator.LK, FilterOperator.IN),
        EXACT(FilterOperator.EQ, FilterOperator.NE, FilterOperator.IN),
        ORDERED(FilterOperator.EQ, FilterOperator.NE, FilterOperator.IN,
                FilterOperator.GT, FilterOperator.LT, FilterOperator.GE, FilterOperator.LE, FilterOperator.BT);

        private final Set<FilterOperator> operators;

        Kind(FilterOperator... operators) {
            this.operators = EnumSet.copyOf(List.of(operators));
        }

        void requireSupported(FilterOperator operator, String key) {
            if (!operators.contains(operator)) {
                throw new BadRequestException("El operador %s no aplica al campo '%s'. Operadores permitidos: %s"
                        .formatted(operator, key, operators.stream().map(Enum::name).collect(Collectors.joining(", "))));
            }
        }
    }

    record Range(Object low, Object high) {

        static Range point(Object value) {
            return new Range(value, value);
        }

        boolean isPoint() {
            return low.equals(high);
        }
    }

    Kind kindOf(Class<?> type, String key) {
        if (String.class.equals(type)) {
            return Kind.TEXT;
        }
        if (Boolean.class.equals(type) || UUID.class.equals(type) || type.isEnum()) {
            return Kind.EXACT;
        }
        if (PARSERS.containsKey(type) || TEMPORAL_CONVERTERS.containsKey(type)) {
            return Kind.ORDERED;
        }
        throw notFilterable(key);
    }

    Range parse(Class<?> type, String raw, String key) {
        try {
            return convert(type, raw, key);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new BadRequestException("Valor '%s' inválido para el filtro '%s'".formatted(raw, key));
        }
    }

    private Range convert(Class<?> type, String raw, String key) {
        if (type.isEnum()) {
            return Range.point(parseEnum(type, raw, key));
        }
        Function<Instant, Object> temporal = TEMPORAL_CONVERTERS.get(type);
        if (temporal != null) {
            return temporalRange(raw, temporal);
        }
        Function<String, Object> parser = PARSERS.get(type);
        if (parser != null) {
            return Range.point(parser.apply(raw));
        }
        throw notFilterable(key);
    }

    private Range temporalRange(String raw, Function<Instant, Object> converter) {
        if (raw.length() == DATE_ONLY_LENGTH) {
            LocalDate day = LocalDate.parse(raw);
            Instant start = day.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant end = day.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().minus(1, ChronoUnit.MICROS);
            return new Range(converter.apply(start), converter.apply(end));
        }
        return Range.point(converter.apply(parseInstant(raw)));
    }

    private Instant parseInstant(String raw) {
        try {
            return OffsetDateTime.parse(raw).toInstant();
        } catch (RuntimeException ex) {
            return LocalDateTime.parse(raw).toInstant(ZoneOffset.UTC);
        }
    }

    private Boolean parseBoolean(String raw) {
        if ("true".equalsIgnoreCase(raw)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(raw)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException(raw);
    }

    private Object parseEnum(Class<?> type, String raw, String key) {
        List<Enum<?>> constants = Arrays.stream(type.getEnumConstants()).<Enum<?>>map(constant -> (Enum<?>) constant).toList();
        return constants.stream()
                .filter(constant -> constant.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Valor '%s' inválido para '%s'. Valores permitidos: %s".formatted(
                        raw,
                        key,
                        constants.stream().map(Enum::name).collect(Collectors.joining(", ")))));
    }

    private BadRequestException notFilterable(String key) {
        return new BadRequestException("El campo '%s' no admite filtros".formatted(key));
    }
}
