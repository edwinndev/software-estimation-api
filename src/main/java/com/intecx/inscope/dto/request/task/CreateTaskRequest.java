package com.intecx.inscope.dto.request.task;

import com.intecx.inscope.enumeration.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
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
public class CreateTaskRequest {

    @NotBlank(message = "El título de la tarea es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String title;

    @NotBlank(message = "La descripción de la tarea es obligatoria")
    private String description;

    @NotNull(message = "La estimación en horas es obligatoria")
    @Positive(message = "La estimación en horas debe ser mayor que cero")
    private Integer estimateHours;

    private TaskStatus status;

    private List<UUID> responsibleProfileIds;
}
