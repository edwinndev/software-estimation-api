package com.intecx.inscope.service.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.inscope.entity.FileAttachment;
import com.intecx.inscope.entity.User;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplAvatarTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000021");

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

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(
                userRepository,
                roleRepository,
                tokenRepository,
                fileAttachmentRepository,
                userMapper,
                passwordEncoder,
                emailService,
                bunnyStorageService
        );
    }

    @Test
    void downloadAvatarReturnsBytesFromBunny() {
        FileAttachment avatar = FileAttachment.builder()
                .storagePath("avatars/" + USER_ID + ".png")
                .build();
        User user = User.builder().id(USER_ID).avatar(avatar).build();
        when(userRepository.findActiveById(USER_ID)).thenReturn(Optional.of(user));
        when(bunnyStorageService.download(avatar.getStoragePath())).thenReturn(new byte[] {9, 8});

        byte[] result = service.downloadAvatar(USER_ID);

        assertArrayEquals(new byte[] {9, 8}, result);
        verify(bunnyStorageService).download(avatar.getStoragePath());
    }

    @Test
    void downloadAvatarThrowsWhenUserHasNoAvatar() {
        when(userRepository.findActiveById(USER_ID))
                .thenReturn(Optional.of(User.builder().id(USER_ID).build()));

        assertThrows(ResourceNotFoundException.class, () -> service.downloadAvatar(USER_ID));
    }
}
