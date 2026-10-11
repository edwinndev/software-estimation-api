package com.intecx.inscope.rest;

import com.intecx.inscope.dto.request.estimation.DurationRequest;
import com.intecx.inscope.dto.request.estimation.VelocityRequest;
import com.intecx.inscope.dto.response.SprintConfigResponse;
import com.intecx.inscope.service.SprintConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Estimación ágil",
        description = "Endpoints para la estimación ágil: story points, velocidad, duración del sprint y horas por tarea"
)
@RestController
@RequestMapping("/api/v1/sprint-configs")
@RequiredArgsConstructor
public class SprintConfigController {

    private final SprintConfigService sprintConfigService;

    @Operation(
            summary = "Registrar la velocidad del equipo",
            description = "Guarda la velocidad obtenida en sprints anteriores. Si el proyecto no tiene "
                    + "configuración de sprint, la crea con los valores por defecto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Velocidad registrada"),
            @ApiResponse(responseCode = "400", description = "Velocidad ausente o inválida"),
            @ApiResponse(responseCode = "409", description = "No se pudo guardar (por ejemplo, el proyecto no existe)")
    })
    @PatchMapping("/{projectId}/velocity")
    @PreAuthorize("hasAuthority('estimation:write')")
    public ResponseEntity<SprintConfigResponse> registerVelocity(
            @PathVariable UUID projectId,
            @Valid @RequestBody VelocityRequest request
    ) {
        return ResponseEntity.ok(sprintConfigService.registerVelocity(projectId, request));
    }

    @Operation(
            summary = "Configurar la duración del sprint",
            description = "Define la duración y la unidad del sprint usadas en las estimaciones. Si el "
                    + "proyecto no tiene configuración de sprint, la crea con los valores por defecto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Duración configurada"),
            @ApiResponse(responseCode = "400", description = "Duración o unidad ausentes o inválidas"),
            @ApiResponse(responseCode = "409", description = "No se pudo guardar (por ejemplo, el proyecto no existe)")
    })
    @PatchMapping("/{projectId}/duration")
    @PreAuthorize("hasAuthority('estimation:write')")
    public ResponseEntity<SprintConfigResponse> configureDuration(
            @PathVariable UUID projectId,
            @Valid @RequestBody DurationRequest request
    ) {
        return ResponseEntity.ok(sprintConfigService.configureDuration(projectId, request));
    }
}