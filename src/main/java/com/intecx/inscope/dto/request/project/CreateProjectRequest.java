package com.intecx.inscope.dto.request.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
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
public class CreateProjectRequest {

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(max = 150, message = "El nombre del proyecto no puede superar los 150 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotBlank(message = "El tipo de proyecto es obligatorio")
    @Size(max = 40, message = "El tipo de proyecto no puede superar los 40 caracteres")
    private String type;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "La fecha prevista es obligatoria")
    private LocalDate plannedDate;

    @NotNull(message = "El responsable del proyecto es obligatorio")
    private UUID responsibleId;
}
