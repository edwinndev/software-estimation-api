package com.intecx.inscope.dto.request.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record UpdateCerRequest(
        @NotNull(message = "El costo estándar (CER) por hora es obligatorio")
        @DecimalMin(value = "0.01", message = "El costo estándar (CER) por hora debe ser mayor a 0")
        @DecimalMax(value = "9999999999.99", message = "El costo estándar (CER) no puede exceder el límite permitido")
        @Digits(integer = 10, fraction = 2, message = "El costo estándar (CER) debe tener como máximo 10 dígitos enteros y 2 decimales")
        @Schema(description = "Costo Estándar por Recurso (CER) por hora", example = "85.00")
        BigDecimal hourlyRate,

        @Pattern(regexp = "^[A-Z]{3}$", message = "La moneda debe tener un formato de 3 letras (ISO 4217)")
        @Schema(description = "Código de moneda ISO", example = "PEN")
        String currency
) {
}
