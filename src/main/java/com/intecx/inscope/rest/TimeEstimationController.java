package com.intecx.inscope.rest;

import com.intecx.inscope.dto.response.TimeEstimationResponse;
import com.intecx.inscope.service.TimeEstimationService;
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
public class TimeEstimationController {

    private final TimeEstimationService timeEstimationService;

    @Operation(
            summary = "Calcular el tiempo estimado del proyecto",
            description = "Devuelve los datos de entrada y el resultado del cálculo: sprints (story points entre "
                    + "velocidad), tiempo calendario (sprints por duración) y esfuerzo total en horas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cálculo realizado"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/{projectId}/time-estimation")
    @PreAuthorize("hasAuthority('estimation:read')")
    public ResponseEntity<TimeEstimationResponse> estimateTime(@PathVariable UUID projectId) {
        return ResponseEntity.ok(timeEstimationService.estimateTime(projectId));
    }
}
