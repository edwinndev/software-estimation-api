package com.intecx.inscope.rest;

import com.intecx.inscope.dto.response.PermissionResponse;
import com.intecx.inscope.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Permisos", description = "Endpoints para la consulta de permisos del sistema")
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Listar todos los permisos del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de permisos obtenido exitosamente")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<PermissionResponse>> getAll() {
        List<PermissionResponse> permissions = permissionService.getAll();
        return ResponseEntity.ok(permissions);
    }
}
