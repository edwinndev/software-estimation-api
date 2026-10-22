package com.intecx.inscope.dto.request.profile;

import com.intecx.inscope.enumeration.CurrencyCode;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTechnicalProfileRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 255, message = "El correo electrónico no debe exceder 255 caracteres")
    private String email;

    @NotNull(message = "El rol técnico es obligatorio")
    private TechnicalRole role;

    @NotNull(message = "El nivel de experiencia es obligatorio")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "El costo estándar (CER) por hora es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo estándar (CER) por hora debe ser mayor a 0")
    @DecimalMax(value = "9999999999.99", message = "El costo estándar (CER) no puede exceder el límite permitido")
    @Digits(integer = 10, fraction = 2, message = "El costo estándar (CER) debe tener como máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal hourlyRate;

    private CurrencyCode currency;

    private Boolean isActive;
}
