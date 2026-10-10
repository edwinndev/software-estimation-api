package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.request.user.CreateUserRequest;
import com.intecx.inscope.dto.response.UserResponse;
import com.intecx.inscope.entity.Role;
import com.intecx.inscope.entity.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper implements BaseMapper<User, UserResponse> {

    @Override
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UUID roleId = user.getRole() != null ? user.getRole().getId() : null;
        String roleCode = user.getRole() != null ? user.getRole().getCode() : null;
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isActive(user.isActive())
                .roleId(roleId)
                .roleCode(roleCode)
                .roleName(roleName)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(CreateUserRequest request, Role role, String encodedPassword, UUID actorId) {
        if (request == null) {
            return null;
        }

        boolean activeStatus = request.getIsActive() == null || request.getIsActive();

        return User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(encodedPassword)
                .role(role)
                .isActive(activeStatus)
                .createdBy(actorId)
                .build();
    }
}
