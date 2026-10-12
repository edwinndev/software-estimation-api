package com.intecx.inscope.rest;


import com.intecx.inscope.dto.response.SprintEstimationResponse;
import com.intecx.inscope.service.SprintEstimationService;
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
public class SprintEstimationController {

    private final SprintEstimationService sprintEstimationService;

    @Operation(
            summary = "Calcular los puntos de historia totales y los sprints estimados",
            description = "Suma los story points de las historias activas del proyecto y los divide entre "
                    + "la velocidad del equipo, redondeando hacia arriba. Si el proyecto no tiene "
                    + "configuración de sprint, usa la velocidad por defecto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cálculo realizado"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/{projectId}/sprint-estimation")
    @PreAuthorize("hasAuthority('estimation:read')")
    public ResponseEntity<SprintEstimationResponse> estimateSprints(@PathVariable UUID projectId) {
        return ResponseEntity.ok(sprintEstimationService.estimateSprints(projectId));
    }
}