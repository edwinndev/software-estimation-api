package com.intecx.estimation.rest;

import com.intecx.estimation.common.PaginatedResponse;
import com.intecx.estimation.common.QueryRequest;
import com.intecx.estimation.dto.request.UserStoryRequest;
import com.intecx.estimation.dto.request.UserStoryUpdateRequest;
import com.intecx.estimation.dto.response.UserStoryResponse;
import com.intecx.estimation.service.UserStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "User stories", description = "Gestión del backlog de historias de usuario")
public class UserStoryController {

    private final UserStoryService userStoryService;

    @PostMapping("/projects/{projectId}/user-stories/search")
    @Operation(summary = "Buscar historias de un proyecto")
    public PaginatedResponse<UserStoryResponse> search(
            @PathVariable UUID projectId,
            @Valid @RequestBody QueryRequest request
    ) {
        return userStoryService.search(projectId, request);
    }

    @PostMapping("/projects/{projectId}/user-stories")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una historia de usuario")
    public UserStoryResponse create(
            @PathVariable UUID projectId,
            @Valid @RequestBody UserStoryRequest request
    ) {
        return userStoryService.create(projectId, request);
    }

    @PutMapping("/user-stories/{storyId}")
    @Operation(summary = "Actualizar una historia de usuario")
    public UserStoryResponse update(
            @PathVariable UUID storyId,
            @Valid @RequestBody UserStoryUpdateRequest request
    ) {
        return userStoryService.update(storyId, request);
    }

    @DeleteMapping("/user-stories/{storyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una historia y sus tareas")
    public void delete(@PathVariable UUID storyId) {
        userStoryService.delete(storyId);
    }
}
