package com.intecx.inscope.service;

import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.repository.ProjectCostProjection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TaskCostHoursResolver {

    private static final int HOURS_SCALE = 2;

    public Map<UUID, BigDecimal> resolve(List<ProjectCostProjection> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            throw new BadRequestException("La tarea debe tener perfiles asignados");
        }
        for (ProjectCostProjection assignment : assignments) {
            if (assignment == null || assignment.getTechnicalProfileId() == null) {
                throw new BadRequestException("La tarea debe tener perfiles asignados");
            }
        }
        List<ProjectCostProjection> ordered = assignments.stream()
                .sorted(Comparator.comparing(row -> row.getTechnicalProfileId().toString()))
                .toList();
        BigDecimal estimate = ordered.getFirst().getEstimateHours();
        if (estimate == null || estimate.signum() < 0) {
            throw new BadRequestException("Las horas estimadas de la tarea deben ser mayores o iguales a cero");
        }
        BigDecimal total = toHoursPrecision(estimate);
        BigDecimal share = total.divide(BigDecimal.valueOf(ordered.size()), HOURS_SCALE, RoundingMode.DOWN);
        BigDecimal remainderShare = total.subtract(share.multiply(BigDecimal.valueOf(ordered.size() - 1L)));
        Map<UUID, BigDecimal> hoursByProfile = new LinkedHashMap<>();
        for (int index = 0; index < ordered.size(); index++) {
            ProjectCostProjection row = ordered.get(index);
            if (row.getEstimateHours() == null || estimate.compareTo(row.getEstimateHours()) != 0) {
                throw new BadRequestException("Las asignaciones de una tarea deben compartir la misma estimación");
            }
            BigDecimal hours = row.getAdjustedHours();
            if (hours == null) {
                hours = index == ordered.size() - 1 ? remainderShare : share;
            }
            if (hours.signum() < 0) {
                throw new BadRequestException("Las horas ajustadas no pueden ser negativas");
            }
            if (hoursByProfile.putIfAbsent(row.getTechnicalProfileId(), hours) != null) {
                throw new BadRequestException("La tarea contiene perfiles asignados duplicados");
            }
        }
        return hoursByProfile;
    }

    private BigDecimal toHoursPrecision(BigDecimal hours) {
        try {
            return hours.setScale(HOURS_SCALE, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new BadRequestException("Las horas de la tarea admiten como máximo dos decimales");
        }
    }
}
