package com.intecx.inscope.rest;

import com.intecx.inscope.dto.request.estimation.VelocityRequest;
import com.intecx.inscope.dto.response.SprintConfigResponse;
import com.intecx.inscope.exception.ErrorResponse;
import com.intecx.inscope.service.SprintConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sprint-configs")
@RequiredArgsConstructor
@Tag(name = "Estimación ágil",
        description = "Endpoints para la estimación ágil: story points, velocidad, duración del sprint y horas por tarea")
public class SprintConfigController {

    private final SprintConfigService sprintConfigService;

    @PutMapping("/{projectId}/velocity")
    @Operation(
            summary = "Registrar la velocidad del equipo",
            description = "Guarda la velocidad obtenida en sprints anteriores. Si el proyecto no tiene "
                    + "configuración de sprint, la crea con los valores por defecto."
    )
    @ApiResponse(responseCode = "200", description = "Velocidad registrada")
    @ApiResponse(
            responseCode = "400",
            description = "Velocidad ausente o inválida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "No se pudo guardar (por ejemplo, el proyecto no existe)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public SprintConfigResponse registerVelocity(
            @PathVariable UUID projectId,
            @Valid @RequestBody VelocityRequest request
    ) {
        return sprintConfigService.registerVelocity(projectId, request);
    }
}