package com.intecx.inscope.dto.request.userstory;

import com.intecx.inscope.enumeration.StoryPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
public class CreateUserStoryRequest {

    @NotBlank(message = "El código de la historia es obligatorio")
    @Size(max = 10, message = "El código no puede superar los 10 caracteres")
    private String code;

    @NotBlank(message = "El título de la historia es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String title;

    @NotBlank(message = "La descripción de la historia es obligatoria")
    private String description;

    private String acceptanceCriteria;

    private StoryPriority priority;

    @PositiveOrZero(message = "Los puntos de historia no pueden ser negativos")
    private Short storyPoints;
}
