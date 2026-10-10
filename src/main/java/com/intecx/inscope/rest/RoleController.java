package com.intecx.inscope.rest;

import com.intecx.inscope.dto.response.RoleResponse;
import com.intecx.inscope.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Roles", description = "Endpoints para la consulta de roles del sistema")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Listar roles del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de roles obtenido exitosamente")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<RoleResponse>> getAll(
            @RequestParam(value = "activeOnly", required = false, defaultValue = "false") boolean activeOnly
    ) {
        List<RoleResponse> roles = activeOnly ? roleService.getAllActive() : roleService.getAll();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Obtener un rol por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<RoleResponse> getById(@PathVariable UUID id) {
        RoleResponse response = roleService.getById(id);
        return ResponseEntity.ok(response);
    }
}
