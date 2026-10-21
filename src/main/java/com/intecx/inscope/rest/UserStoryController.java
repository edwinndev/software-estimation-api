package com.intecx.inscope.rest;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.userstory.CreateUserStoryRequest;
import com.intecx.inscope.dto.request.userstory.UpdateUserStoryRequest;
import com.intecx.inscope.dto.response.UserStoryResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.UserStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Historias de usuario", description = "Endpoints para la gestión de historias de usuario del backlog")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserStoryController {

    private final UserStoryService userStoryService;

    @Operation(summary = "Búsqueda paginada de historias de un proyecto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado obtenido exitosamente"),
            @ApiResponse(responseCode = "400", description = "Filtros o criterios de búsqueda inválidos"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @PostMapping("/projects/{projectId}/user-stories/search")
    @PreAuthorize("hasAuthority('backlog:read')")
    public ResponseEntity<PaginatedResponse<UserStoryResponse>> searchByProject(
            @PathVariable UUID projectId,
            @RequestBody(required = false) QueryRequest query
    ) {
        return ResponseEntity.ok(userStoryService.searchByProject(projectId, query));
    }

    @Operation(summary = "Crear una historia de usuario en un proyecto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Historia creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado"),
            @ApiResponse(responseCode = "409", description = "El código de historia ya está en uso")
    })
    @PostMapping("/projects/{projectId}/user-stories")
    @PreAuthorize("hasAuthority('backlog:write')")
    public ResponseEntity<UserStoryResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateUserStoryRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserStoryResponse response = userStoryService.create(projectId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener una historia de usuario por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historia encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Historia no encontrada")
    })
    @GetMapping("/user-stories/{id}")
    @PreAuthorize("hasAuthority('backlog:read')")
    public ResponseEntity<UserStoryResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userStoryService.findById(id));
    }

    @Operation(summary = "Actualizar una historia de usuario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historia actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Historia no encontrada"),
            @ApiResponse(responseCode = "409", description = "El código de historia ya está en uso")
    })
    @PutMapping("/user-stories/{id}")
    @PreAuthorize("hasAuthority('backlog:write')")
    public ResponseEntity<UserStoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStoryRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(userStoryService.update(id, request, principal.getId()));
    }

    @Operation(summary = "Eliminar una historia de usuario (borrado lógico)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Historia eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Historia no encontrada"),
            @ApiResponse(responseCode = "409", description = "La historia conserva tareas activas")
    })
    @DeleteMapping("/user-stories/{id}")
    @PreAuthorize("hasAuthority('backlog:delete')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userStoryService.delete(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
