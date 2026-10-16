package com.intecx.inscope.rest;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.task.CreateTaskRequest;
import com.intecx.inscope.dto.request.task.UpdateTaskRequest;
import com.intecx.inscope.dto.request.task.UpdateTaskResponsiblesRequest;
import com.intecx.inscope.dto.response.TaskResponse;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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

@Tag(name = "Tareas", description = "Gestión de tareas y perfiles responsables")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Buscar tareas de una historia")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado obtenido"),
            @ApiResponse(responseCode = "404", description = "Historia no encontrada")
    })
    @PostMapping("/user-stories/{storyId}/tasks/search")
    @PreAuthorize("hasAuthority('backlog:read')")
    public ResponseEntity<PaginatedResponse<TaskResponse>> searchByStory(
            @PathVariable UUID storyId,
            @RequestBody(required = false) QueryRequest query
    ) {
        return ResponseEntity.ok(taskService.searchByStory(storyId, query));
    }

    @Operation(summary = "Buscar tareas de un proyecto a través de sus historias")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado obtenido"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @PostMapping("/projects/{projectId}/tasks/search")
    @PreAuthorize("hasAuthority('backlog:read')")
    public ResponseEntity<PaginatedResponse<TaskResponse>> searchByProject(
            @PathVariable UUID projectId,
            @RequestBody(required = false) QueryRequest query
    ) {
        return ResponseEntity.ok(taskService.searchByProject(projectId, query));
    }

    @Operation(summary = "Crear tarea dentro de una historia")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Historia o perfil no encontrado")
    })
    @PostMapping("/user-stories/{storyId}/tasks")
    @PreAuthorize("hasAuthority('backlog:write')")
    public ResponseEntity<TaskResponse> create(
            @PathVariable UUID storyId,
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        TaskResponse response = taskService.create(storyId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Consultar una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea obtenida"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasAuthority('backlog:read')")
    public ResponseEntity<TaskResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @Operation(summary = "Actualizar parcialmente una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PatchMapping("/tasks/{id}")
    @PreAuthorize("hasAuthority('backlog:write')")
    public ResponseEntity<TaskResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(taskService.update(id, request, principal.getId()));
    }

    @Operation(summary = "Consultar perfiles técnicos responsables de una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Responsables obtenidos"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @GetMapping("/tasks/{id}/responsibles")
    @PreAuthorize("hasAuthority('backlog:read')")
    public ResponseEntity<List<TechnicalProfileResponse>> getResponsibles(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getResponsibles(id));
    }

    @Operation(summary = "Reemplazar los perfiles técnicos responsables de una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Responsables actualizados"),
            @ApiResponse(responseCode = "400", description = "Lista inválida o duplicada"),
            @ApiResponse(responseCode = "404", description = "Tarea o perfil no encontrado")
    })
    @PutMapping("/tasks/{id}/responsibles")
    @PreAuthorize("hasAuthority('backlog:write')")
    public ResponseEntity<TaskResponse> updateResponsibles(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskResponsiblesRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(taskService.updateResponsibles(
                id,
                request.responsibleProfileIds(),
                principal.getId()
        ));
    }

    @Operation(summary = "Eliminar lógicamente una tarea")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarea eliminada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasAuthority('backlog:delete')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        taskService.delete(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}