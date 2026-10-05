package com.intecx.inscope.rest;

import com.intecx.inscope.dto.request.auth.ChangePasswordRequest;
import com.intecx.inscope.dto.request.auth.LoginRequest;
import com.intecx.inscope.dto.request.auth.RefreshTokenRequest;
import com.intecx.inscope.dto.request.auth.RequestPasswordResetRequest;
import com.intecx.inscope.dto.request.auth.ResetPasswordRequest;
import com.intecx.inscope.dto.request.auth.UpdateProfileRequest;
import com.intecx.inscope.dto.request.auth.VerifyResetCodeRequest;
import com.intecx.inscope.dto.response.AuthResponse;
import com.intecx.inscope.dto.response.MessageResponse;
import com.intecx.inscope.dto.response.UserResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticación", description = "Endpoints de inicio de sesión, perfil, OTP y cierre de sesión")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Iniciar sesión y obtener credenciales JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión iniciada correctamente"),
            @ApiResponse(responseCode = "401", description = "Credenciales o correo incorrectos")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Renovar el token de acceso mediante el refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token de refresco inválido o expirado")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cerrar sesión e invalidar la sesión/token activo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente")
    })
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        MessageResponse response = authService.logout(bearerToken);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener el perfil del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserResponse response = authService.getCurrentProfile(principal.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar el perfil del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El correo electrónico ya está en uso")
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserResponse response = authService.updateCurrentProfile(principal.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar la contraseña del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "La contraseña actual es incorrecta o inválida")
    })
    @PutMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        MessageResponse response = authService.changePassword(principal.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Solicitar código de verificación OTP para restablecimiento de contraseña")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código OTP enviado exitosamente al correo"),
            @ApiResponse(responseCode = "400", description = "Cuenta inactiva o datos no válidos")
    })
    @PostMapping("/password-reset/request")
    public ResponseEntity<MessageResponse> requestPasswordReset(@Valid @RequestBody RequestPasswordResetRequest request) {
        MessageResponse response = authService.requestPasswordReset(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verificar código OTP de restablecimiento de contraseña")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código OTP verificado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Código OTP incorrecto o expirado")
    })
    @PostMapping("/password-reset/verify")
    public ResponseEntity<MessageResponse> verifyResetCode(@Valid @RequestBody VerifyResetCodeRequest request) {
        MessageResponse response = authService.verifyResetCode(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Restablecer contraseña con código OTP verificado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente"),
            @ApiResponse(responseCode = "400", description = "El código OTP debe verificarse previamente")
    })
    @PostMapping("/password-reset/reset")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}
