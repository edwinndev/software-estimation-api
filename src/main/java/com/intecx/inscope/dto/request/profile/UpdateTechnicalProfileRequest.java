package com.intecx.inscope.dto.request.profile;

import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateTechnicalProfileRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @Schema(description = "Nombre completo del perfil técnico", example = "Juan Pérez")
        String name,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo electrónico no tiene un formato válido")
        @Size(max = 255, message = "El correo electrónico no debe exceder 255 caracteres")
        @Schema(description = "Correo electrónico único", example = "juan.perez@empresa.com")
        String email,

        @NotNull(message = "El rol técnico es obligatorio")
        @Schema(description = "Rol técnico desempeñado", example = "FRONTEND")
        TechnicalRole role,

        @NotNull(message = "El nivel de experiencia es obligatorio")
        @Schema(description = "Nivel de experiencia", example = "SENIOR")
        ExperienceLevel experienceLevel,

        @NotNull(message = "El costo estándar (CER) por hora es obligatorio")
        @DecimalMin(value = "0.01", message = "El costo estándar (CER) por hora debe ser mayor a 0")
        @DecimalMax(value = "9999999999.99", message = "El costo estándar (CER) no puede exceder el límite permitido")
        @Digits(integer = 10, fraction = 2, message = "El costo estándar (CER) debe tener como máximo 10 dígitos enteros y 2 decimales")
        @Schema(description = "Costo Estándar por Recurso (CER) por hora", example = "85.00")
        BigDecimal hourlyRate,

        @Pattern(regexp = "^[A-Z]{3}$", message = "La moneda debe tener un formato de 3 letras (ISO 4217)")
        @Schema(description = "Código de moneda ISO", example = "PEN")
        String currency,

        @Schema(description = "Indica si el perfil se encuentra activo", example = "true")
        Boolean isActive
) {
}
