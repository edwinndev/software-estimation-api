package com.intecx.inscope.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.inscope.dto.request.user.CreateUserRequest;
import com.intecx.inscope.dto.request.user.ToggleUserStatusRequest;
import com.intecx.inscope.dto.response.UserResponse;
import com.intecx.inscope.entity.Role;
import com.intecx.inscope.entity.User;
import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.UserMapper;
import com.intecx.inscope.repository.FileAttachmentRepository;
import com.intecx.inscope.repository.PasswordResetTokenRepository;
import com.intecx.inscope.repository.RoleRepository;
import com.intecx.inscope.repository.UserRepository;
import com.intecx.inscope.service.BunnyStorageService;
import com.intecx.inscope.service.EmailService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private FileAttachmentRepository fileAttachmentRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private BunnyStorageService bunnyStorageService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;
    private UUID userId;
    private UUID roleId;
    private UUID actorId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        actorId = UUID.randomUUID();

        role = Role.builder()
                .id(roleId)
                .code("DEV")
                .name("Developer")
                .isSystem(false)
                .isActive(true)
                .build();

        user = User.builder()
                .id(userId)
                .firstName("Neil")
                .lastName("Armstrong")
                .email("neil.armstrong@intecx.pe")
                .passwordHash("hashed_password")
                .role(role)
                .isActive(true)
                .build();
    }

    @Test
    void getById_Success() {
        when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build());

        UserResponse response = userService.getById(userId);

        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals("neil.armstrong@intecx.pe", response.email());
        verify(userRepository).findActiveById(userId);
    }

    @Test
    void getById_NotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findActiveById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void create_Success() {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("Neil")
                .lastName("Armstrong")
                .email("neil.armstrong@intecx.pe")
                .roleId(roleId)
                .isActive(true)
                .build();

        when(userRepository.existsActiveByEmail("neil.armstrong@intecx.pe")).thenReturn(false);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(userMapper.toEntity(eq(request), eq(role), anyString(), eq(actorId))).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build());

        UserResponse response = userService.create(request, actorId);

        assertNotNull(response);
        verify(userRepository).save(user);
        verify(emailService).sendInvitationOtpEmail(
                eq("neil.armstrong@intecx.pe"),
                eq("Neil"),
                anyString(),
                anyInt()
        );
    }

    @Test
    void create_DuplicateEmail_ThrowsConflictException() {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("neil.armstrong@intecx.pe")
                .roleId(roleId)
                .build();

        when(userRepository.existsActiveByEmail("neil.armstrong@intecx.pe")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(request, actorId));
    }

    @Test
    void changeStatus_Success() {
        ToggleUserStatusRequest request = ToggleUserStatusRequest.builder()
                .isActive(false)
                .build();

        when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .id(user.getId())
                .isActive(false)
                .build());

        UserResponse response = userService.changeStatus(userId, request, actorId);

        assertNotNull(response);
        assertFalse(response.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void changeStatus_SystemRole_ThrowsBadRequestException() {
        Role systemRole = Role.builder()
                .id(UUID.randomUUID())
                .code("SUPER_ADMIN")
                .isSystem(true)
                .build();
        user.setRole(systemRole);

        ToggleUserStatusRequest request = ToggleUserStatusRequest.builder()
                .isActive(false)
                .build();

        when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.changeStatus(userId, request, actorId));
    }
}

