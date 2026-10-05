package com.intecx.inscope.service.impl;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryFields;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.common.QuerySupport;
import com.intecx.inscope.dto.request.user.CreateUserRequest;
import com.intecx.inscope.dto.request.user.ToggleUserStatusRequest;
import com.intecx.inscope.dto.request.user.UpdateUserRequest;
import com.intecx.inscope.dto.response.MessageResponse;
import com.intecx.inscope.dto.response.UserResponse;
import com.intecx.inscope.entity.Role;
import com.intecx.inscope.entity.User;
import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.UserMapper;
import com.intecx.inscope.entity.PasswordResetToken;
import com.intecx.inscope.repository.PasswordResetTokenRepository;
import com.intecx.inscope.repository.RoleRepository;
import com.intecx.inscope.repository.UserRepository;
import com.intecx.inscope.service.EmailService;
import com.intecx.inscope.service.UserService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final QueryFields USER_FIELDS = QueryFields.sortingBy("createdAt")
            .filter("search", "firstName", "lastName", "email")
            .filter("firstName", "firstName")
            .filter("lastName", "lastName")
            .filter("email", "email")
            .filter("isActive", "isActive")
            .filter("role", "role.code")
            .filter("createdAt", "createdAt")
            .sortable("createdAt", "firstName", "lastName", "email", "isActive");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<UserResponse> search(QueryRequest query) {
        Specification<User> notDeletedScope = (root, q, cb) -> cb.isNull(root.get("deletedAt"));
        return QuerySupport.search(userRepository, notDeletedScope, USER_FIELDS, query, userMapper::toResponse, "userResponse");
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request, UUID actorId) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsActiveByEmail(email)) {
            throw new ConflictException("Ya existe un usuario registrado con el correo electrónico proporcionado");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("El rol especificado no existe"));

        if (role.isSystem()) {
            throw new BadRequestException("No se permite asignar roles del sistema a usuarios nuevos");
        }

        String temporaryPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);
        User user = userMapper.toEntity(request, role, encodedPassword, actorId);

        User savedUser = userRepository.save(user);

        String otpCode = generateAndSaveOtpToken(savedUser.getEmail());
        emailService.sendInvitationOtpEmail(savedUser.getEmail(), savedUser.getFirstName(), otpCode);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request, UUID actorId) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String newEmail = request.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsActiveByEmailAndIdNot(newEmail, id)) {
            throw new ConflictException("Ya existe un usuario registrado con el correo electrónico proporcionado");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("El rol especificado no existe"));

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(newEmail);
        user.setRole(role);
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }
        user.setUpdatedBy(actorId);

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse changeStatus(UUID id, ToggleUserStatusRequest request, UUID actorId) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getRole() != null && user.getRole().isSystem() && Boolean.FALSE.equals(request.getIsActive())) {
            throw new BadRequestException("No se puede desactivar al usuario administrador del sistema");
        }

        user.setActive(request.getIsActive());
        user.setUpdatedBy(actorId);

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public MessageResponse resendInvitation(UUID id, UUID actorId) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!user.isActive()) {
            throw new BadRequestException("No se puede enviar invitación a un usuario desactivado");
        }

        String otpCode = generateAndSaveOtpToken(user.getEmail());
        emailService.sendInvitationOtpEmail(user.getEmail(), user.getFirstName(), otpCode);

        return new MessageResponse("Invitación reenviada exitosamente a " + user.getEmail() + " con código OTP de activación");
    }

    private String generateAndSaveOtpToken(String email) {
        String otpCode = String.format("%06d", secureRandom.nextInt(1000000));
        String codeHash = passwordEncoder.encode(otpCode);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .codeHash(codeHash)
                .expiresAt(expiresAt)
                .build();

        tokenRepository.save(resetToken);
        return otpCode;
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID actorId) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getRole() != null && user.getRole().isSystem()) {
            throw new BadRequestException("No se puede eliminar al usuario administrador del sistema");
        }
        if (user.getId().equals(actorId)) {
            throw new BadRequestException("No puedes eliminar tu propia cuenta de usuario");
        }

        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(actorId);
        user.setActive(false);

        userRepository.save(user);
    }
}
