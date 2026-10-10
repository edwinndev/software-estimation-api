package com.intecx.inscope.rest;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.project.CreateProjectRequest;
import com.intecx.inscope.dto.request.project.UpdateProjectRequest;
import com.intecx.inscope.dto.request.project.UpdateProjectStatusRequest;
import com.intecx.inscope.dto.response.ProjectResponse;
import com.intecx.inscope.dto.response.ProjectStatusHistoryResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Proyectos", description = "Endpoints para la gestión de proyectos")
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Buscar proyectos con sus estimaciones guardadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado de proyectos obtenido"),
            @ApiResponse(responseCode = "400", description = "Filtros o criterios de búsqueda inválidos")
    })
    @PostMapping("/search")
    @PreAuthorize("hasAuthority('project:read')")
    public ResponseEntity<PaginatedResponse<ProjectResponse>> search(
            @RequestBody(required = false) QueryRequest query
    ) {
        PaginatedResponse<ProjectResponse> response = projectService.search(query);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar un proyecto en estado Borrador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Proyecto registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Responsable no encontrado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('project:write')")
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProjectResponse response = projectService.create(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar un proyecto en estado Borrador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proyecto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Proyecto o responsable no encontrado"),
            @ApiResponse(responseCode = "409", description = "El proyecto no se encuentra en estado Borrador")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('project:write')")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProjectResponse response = projectService.update(id, request, principal.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar el estado de un proyecto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del proyecto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El estado del proyecto es obligatorio"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado"),
            @ApiResponse(responseCode = "409", description = "La transición de estado no está permitida")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('project:write')")
    public ResponseEntity<ProjectResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProjectResponse response = projectService.updateStatus(id, request, principal.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar el historial de cambios de estado de un proyecto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial de estados obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/{id}/status-history")
    @PreAuthorize("hasAuthority('project:read')")
    public ResponseEntity<List<ProjectStatusHistoryResponse>> getStatusHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getStatusHistory(id));
    }

    @Operation(summary = "Eliminar un proyecto en estado Borrador o Rechazado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Proyecto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado"),
            @ApiResponse(responseCode = "409", description = "El estado actual no permite eliminar el proyecto")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('project:delete')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        projectService.delete(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar una estimación de un proyecto en estado Borrador o Rechazado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estimación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Proyecto o estimación no encontrado"),
            @ApiResponse(responseCode = "409", description = "El estado del proyecto no permite eliminar la estimación")
    })
    @DeleteMapping("/{projectId}/estimations/{estimationId}")
    @PreAuthorize("hasAuthority('estimation:delete')")
    public ResponseEntity<Void> deleteEstimation(
            @PathVariable UUID projectId,
            @PathVariable UUID estimationId
    ) {
        projectService.deleteEstimation(projectId, estimationId);
        return ResponseEntity.noContent().build();
    }
}
