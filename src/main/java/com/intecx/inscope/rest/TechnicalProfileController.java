package com.intecx.inscope.rest;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.request.profile.ToggleProfileStatusRequest;
import com.intecx.inscope.dto.request.profile.UpdateCerRequest;
import com.intecx.inscope.dto.request.profile.UpdateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.TechnicalProfileService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Perfiles técnicos", description = "Endpoints para la gestión de perfiles técnicos y Costo Estándar por Recurso (CER)")
@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class TechnicalProfileController {

    private final TechnicalProfileService technicalProfileService;

    @Operation(summary = "Búsqueda paginada y filtrado de perfiles técnicos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado obtenido exitosamente"),
            @ApiResponse(responseCode = "400", description = "Filtros o criterios de búsqueda inválidos")
    })
    @PostMapping("/search")
    @PreAuthorize("hasAuthority('profile:read')")
    public ResponseEntity<PaginatedResponse<TechnicalProfileResponse>> search(
            @RequestBody(required = false) QueryRequest query) {
        PaginatedResponse<TechnicalProfileResponse> response = technicalProfileService.search(query);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener un perfil técnico por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil técnico encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil técnico no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('profile:read')")
    public ResponseEntity<TechnicalProfileResponse> getById(@PathVariable UUID id) {
        TechnicalProfileResponse response = technicalProfileService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar un perfil técnico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil técnico registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('profile:write')")
    public ResponseEntity<TechnicalProfileResponse> create(
            @Valid @RequestBody CreateTechnicalProfileRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        TechnicalProfileResponse response = technicalProfileService.create(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar un perfil técnico existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil técnico actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('profile:write')")
    public ResponseEntity<TechnicalProfileResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTechnicalProfileRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        TechnicalProfileResponse response = technicalProfileService.update(id, request, principal.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Asignar o actualizar el Costo Estándar por Recurso (CER) por hora")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CER asignado/actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PatchMapping("/{id}/cer")
    @PreAuthorize("hasAuthority('cost:write') or hasAuthority('profile:write')")
    public ResponseEntity<TechnicalProfileResponse> updateCer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        TechnicalProfileResponse response = technicalProfileService.updateCer(id, request, principal.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar o desactivar un perfil técnico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del perfil técnico actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('profile:write')")
    public ResponseEntity<TechnicalProfileResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ToggleProfileStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        TechnicalProfileResponse response = technicalProfileService.changeStatus(id, request, principal.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar un perfil técnico (borrado lógico)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil técnico eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Perfil técnico no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('profile:delete')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        technicalProfileService.delete(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
