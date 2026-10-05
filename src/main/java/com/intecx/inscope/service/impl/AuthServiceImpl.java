package com.intecx.inscope.service.impl;

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
import com.intecx.inscope.entity.PasswordResetToken;
import com.intecx.inscope.entity.User;
import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.exception.UnauthorizedException;
import com.intecx.inscope.mapper.UserMapper;
import com.intecx.inscope.repository.PasswordResetTokenRepository;
import com.intecx.inscope.repository.UserRepository;
import com.intecx.inscope.security.JwtTokenBlacklistService;
import com.intecx.inscope.security.JwtTokenProvider;
import com.intecx.inscope.service.AuthService;
import com.intecx.inscope.service.EmailService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String TOKEN_TYPE_BEARER = "Bearer";
    private static final int OTP_EXPIRATION_MINUTES = 10;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final JwtTokenBlacklistService blacklistService;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findActiveByEmail(request.getEmail().trim())
                .orElseThrow(() -> new UnauthorizedException("Correo electrónico o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Correo electrónico o contraseña incorrectos");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("La cuenta de usuario está desactivada. Por favor contacte al administrador.");
        }

        String token = tokenProvider.generateToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);
        long expiresIn = tokenProvider.getExpirationInSeconds();
        List<String> permissions = getUserPermissions(user);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType(TOKEN_TYPE_BEARER)
                .expiresInSeconds(expiresIn)
                .user(userMapper.toResponse(user))
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenStr = request.getRefreshToken().trim();

        if (!tokenProvider.validateToken(refreshTokenStr) || blacklistService.isBlacklisted(refreshTokenStr)) {
            throw new UnauthorizedException("El token de refresco no es válido o ha expirado");
        }

        String email = tokenProvider.getEmailFromToken(refreshTokenStr);
        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        if (!user.isActive()) {
            throw new UnauthorizedException("La cuenta de usuario está desactivada");
        }

        String newToken = tokenProvider.generateToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);
        long expiresIn = tokenProvider.getExpirationInSeconds();
        List<String> permissions = getUserPermissions(user);

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .tokenType(TOKEN_TYPE_BEARER)
                .expiresInSeconds(expiresIn)
                .user(userMapper.toResponse(user))
                .permissions(permissions)
                .build();
    }

    private List<String> getUserPermissions(User user) {
        if (user.getRole() != null && user.getRole().getPermissions() != null) {
            return user.getRole().getPermissions().stream()
                    .map(com.intecx.inscope.entity.Permission::getCode)
                    .toList();
        }
        return List.of();
    }

    @Override
    public MessageResponse logout(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            if (tokenProvider.validateToken(token)) {
                Date expirationDate = tokenProvider.getExpirationDateFromToken(token);
                blacklistService.blacklistToken(token, expirationDate);
            }
        }
        return new MessageResponse("Sesión cerrada exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentProfile(String email) {
        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findActiveByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String newEmail = request.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsActiveByEmailAndIdNot(newEmail, user.getId())) {
            throw new ConflictException("Ya existe un usuario registrado con el correo electrónico proporcionado");
        }

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(newEmail);
        user.setUpdatedBy(user.getId());

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public MessageResponse changePassword(String currentEmail, ChangePasswordRequest request) {
        User user = userRepository.findActiveByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("La contraseña actual es incorrecta");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedBy(user.getId());
        userRepository.save(user);

        return new MessageResponse("Contraseña actualizada exitosamente");
    }

    @Override
    @Transactional
    public MessageResponse requestPasswordReset(RequestPasswordResetRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una cuenta con este correo electrónico"));

        if (!user.isActive()) {
            throw new BadRequestException("La cuenta de usuario está desactivada");
        }

        String otpCode = String.format("%06d", secureRandom.nextInt(1000000));
        String codeHash = passwordEncoder.encode(otpCode);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .codeHash(codeHash)
                .expiresAt(expiresAt)
                .build();

        tokenRepository.save(resetToken);

        log.info("[OTP] Código de verificación generado para {}: {}", email, otpCode);
        emailService.sendOtpEmail(email, otpCode);

        return new MessageResponse("Código de verificación enviado al correo electrónico");
    }

    @Override
    @Transactional
    public MessageResponse verifyResetCode(VerifyResetCodeRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String code = request.getCode().trim();

        PasswordResetToken resetToken = tokenRepository
                .findLatestActiveByEmail(email)
                .orElseThrow(() -> new BadRequestException("Solicita un código de verificación primero"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El código expiró. Solicita uno nuevo");
        }

        if (!passwordEncoder.matches(code, resetToken.getCodeHash())) {
            throw new BadRequestException("El código no es válido");
        }

        resetToken.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);

        return new MessageResponse("Código de verificación verificado exitosamente");
    }

    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String code = request.getCode().trim();

        PasswordResetToken resetToken = tokenRepository
                .findLatestVerifiedByEmail(email)
                .orElseThrow(() -> new BadRequestException("Debes verificar el código primero"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El código expiró. Solicita uno nuevo");
        }

        if (!passwordEncoder.matches(code, resetToken.getCodeHash())) {
            throw new BadRequestException("El código no es válido");
        }

        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una cuenta con este correo electrónico"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedBy(user.getId());
        userRepository.save(user);

        resetToken.setConsumedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);

        return new MessageResponse("Contraseña restablecida exitosamente");
    }
}
