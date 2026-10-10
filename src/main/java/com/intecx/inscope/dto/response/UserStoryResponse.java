package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.StoryPriority;
import com.intecx.inscope.enumeration.StoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Historia de usuario del backlog de un proyecto")
public class UserStoryResponse {

    @Schema(description = "Identificador de la historia")
    UUID id;

    @Schema(description = "Identificador del proyecto al que pertenece")
    UUID projectId;

    @Schema(description = "Código único dentro del proyecto", example = "US-001")
    String code;

    @Schema(description = "Título de la historia")
    String title;

    @Schema(description = "Descripción de la historia")
    String description;

    @Schema(description = "Prioridad de la historia")
    StoryPriority priority;

    @Schema(description = "Estado de la historia")
    StoryStatus status;

    @Schema(description = "Story points asignados; nulo si aún no se estima", example = "5")
    Short storyPoints;

    @Schema(description = "Posición de la historia dentro del backlog")
    Integer position;
}