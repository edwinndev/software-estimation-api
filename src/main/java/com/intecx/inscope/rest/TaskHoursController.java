package com.intecx.inscope.rest;

import com.intecx.inscope.dto.response.TaskHoursResponse;
import com.intecx.inscope.service.TaskHoursService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Estimación ágil",
        description = "Endpoints para la estimación ágil: story points, velocidad, duración del sprint y horas por tarea"
)
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class TaskHoursController {

    private final TaskHoursService taskHoursService;

    @Operation(
            summary = "Obtener las horas estimadas por tarea de un proyecto",
            description = "Devuelve, por historia, las horas de cada tarea repartidas entre los perfiles "
                    + "asignados (o ajustadas manualmente) y el esfuerzo total del proyecto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horas por tarea del proyecto"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/{projectId}/task-hours")
    @PreAuthorize("hasAuthority('estimation:read')")
    public ResponseEntity<TaskHoursResponse> getTaskHours(@PathVariable UUID projectId) {
        return ResponseEntity.ok(taskHoursService.getTaskHours(projectId));
    }
}