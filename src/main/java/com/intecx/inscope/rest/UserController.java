package com.intecx.inscope.rest;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.user.CreateUserRequest;
import com.intecx.inscope.dto.request.user.ToggleUserStatusRequest;
import com.intecx.inscope.dto.request.user.UpdateUserRequest;
import com.intecx.inscope.dto.response.MessageResponse;
import com.intecx.inscope.dto.response.UserResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.UserService;
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

@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios del sistema")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Búsqueda paginada y filtrado de usuarios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado obtenido exitosamente"),
            @ApiResponse(responseCode = "400", description = "Filtros o criterios de búsqueda inválidos")
    })
    @PostMapping("/search")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<PaginatedResponse<UserResponse>> search(@RequestBody(required = false) QueryRequest query) {
        PaginatedResponse<UserResponse> response = userService.search(query);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener un usuario por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        UserResponse response = userService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear un nuevo usuario por administración")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente e invitación OTP enviada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El correo electrónico ya está en uso")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse response = userService.create(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar un usuario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "El nuevo correo electrónico ya está en uso")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse response = userService.update(id, request, principal.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar o desactivar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede desactivar al usuario administrador del sistema"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<UserResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ToggleUserStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse response = userService.changeStatus(id, request, principal.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reenviar invitación o correo de bienvenida a un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invitación reenviada exitosamente con código OTP"),
            @ApiResponse(responseCode = "400", description = "No se puede invitar a un usuario desactivado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/{id}/resend-invitation")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<MessageResponse> resendInvitation(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MessageResponse response = userService.resendInvitation(id, principal.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar un usuario (borrado lógico)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar al administrador o a la cuenta propia"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.delete(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
