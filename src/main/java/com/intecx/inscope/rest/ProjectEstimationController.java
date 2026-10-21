package com.intecx.inscope.rest;

import com.intecx.inscope.dto.response.ProjectEstimationResponse;
import com.intecx.inscope.dto.response.ReportSnapshotDetailResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.ProjectEstimationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Estimaciones", description = "Consolidación, registro y consulta de estimaciones por proyecto")
@RestController
@RequestMapping("/api/v1/projects/{projectId}")
@RequiredArgsConstructor
public class ProjectEstimationController {

    private final ProjectEstimationService projectEstimationService;

    @Operation(summary = "Consolidar esfuerzo, tiempo y costo estimados (sin guardar)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consolidación calculada"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/estimation-summary")
    @PreAuthorize("hasAuthority('estimation:read')")
    public ResponseEntity<ReportSnapshotDetailResponse> consolidate(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectEstimationService.consolidate(projectId));
    }

    @Operation(summary = "Registrar una estimación del proyecto con fecha, usuario y datos de entrada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estimación registrada"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @PostMapping("/estimations")
    @PreAuthorize("hasAuthority('estimation:write')")
    public ResponseEntity<ReportSnapshotDetailResponse> create(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectEstimationService.create(projectId, principal.getId()));
    }

    @Operation(summary = "Listar estimaciones registradas de un proyecto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de estimaciones"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/estimations")
    @PreAuthorize("hasAuthority('estimation:read')")
    public ResponseEntity<List<ProjectEstimationResponse>> list(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectEstimationService.listByProject(projectId));
    }

    @Operation(summary = "Consultar detalle de una estimación (base y con contingencia)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle de la estimación"),
            @ApiResponse(responseCode = "404", description = "Proyecto o estimación no encontrado")
    })
    @GetMapping("/estimations/{estimationId}")
    @PreAuthorize("hasAuthority('estimation:read')")
    public ResponseEntity<ReportSnapshotDetailResponse> getById(
            @PathVariable UUID projectId,
            @PathVariable UUID estimationId
    ) {
        return ResponseEntity.ok(projectEstimationService.getById(projectId, estimationId));
    }
}
