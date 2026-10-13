package com.intecx.inscope.dto.request.estimation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Ajuste manual de las horas de un perfil técnico en una tarea")
public record HourAdjustmentRequest(
        @Schema(description = "Horas ajustadas del perfil en la tarea", example = "6.50")
        @NotNull(message = "Las horas son obligatorias")
        @PositiveOrZero(message = "Las horas no pueden ser negativas")
        @Digits(integer = 6, fraction = 2, message = "Las horas admiten hasta 6 enteros y 2 decimales")
        BigDecimal hours,

        @Schema(description = "Motivo del ajuste", example = "menor duración")
        @NotBlank(message = "El motivo del ajuste es obligatorio")
        @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
        String reason
) {
}
