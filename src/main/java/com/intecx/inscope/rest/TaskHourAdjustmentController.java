package com.intecx.inscope.rest;

import com.intecx.inscope.dto.request.estimation.HourAdjustmentRequest;
import com.intecx.inscope.dto.response.TaskProfileHoursResponse;
import com.intecx.inscope.service.TaskHourAdjustmentService;
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
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskHourAdjustmentController {

    private final TaskHourAdjustmentService taskHourAdjustmentService;

    @Operation(
            summary = "Ajustar manualmente las horas de un perfil en una tarea",
            description = "Reemplaza las horas del reparto automático para el perfil indicado y registra el "
                    + "motivo del cambio. El perfil debe estar asignado a la tarea."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horas ajustadas"),
            @ApiResponse(responseCode = "400", description = "Horas o motivo ausentes o inválidos"),
            @ApiResponse(responseCode = "404", description = "La tarea o la asignación del perfil no existe")
    })
    @PatchMapping("/{taskId}/profiles/{profileId}/hours")
    @PreAuthorize("hasAuthority('estimation:write')")
    public ResponseEntity<TaskProfileHoursResponse> adjustHours(
            @PathVariable UUID taskId,
            @PathVariable UUID profileId,
            @Valid @RequestBody HourAdjustmentRequest request
    ) {
        return ResponseEntity.ok(taskHourAdjustmentService.adjustHours(taskId, profileId, request));
    }
}