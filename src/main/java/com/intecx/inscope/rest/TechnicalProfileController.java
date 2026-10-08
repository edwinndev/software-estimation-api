package com.intecx.inscope.rest;

import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.TechnicalProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Perfiles técnicos", description = "Endpoints para la gestión de perfiles técnicos y Costo Estándar por Recurso (CER)")
@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class TechnicalProfileController {

    private final TechnicalProfileService technicalProfileService;

    @Operation(summary = "Registrar un perfil técnico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil técnico registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El correo electrónico ya está en uso")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('profile:write')")
    public ResponseEntity<TechnicalProfileResponse> create(
            @Valid @RequestBody CreateTechnicalProfileRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        TechnicalProfileResponse response = technicalProfileService.create(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
