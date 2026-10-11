package com.intecx.inscope.rest;

import com.intecx.inscope.dto.request.estimation.StoryPointsRequest;
import com.intecx.inscope.dto.response.UserStoryResponse;
import com.intecx.inscope.service.UserStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Estimación ágil",
        description = "Endpoints para la estimación ágil: story points, velocidad, duración del sprint y horas por tarea"
)
@RestController
@RequestMapping("/api/v1/user-stories")
@RequiredArgsConstructor
public class UserStoryController {

    private final UserStoryService userStoryService;

    @Operation(
            summary = "Asignar story points a una historia de usuario",
            description = "Registra o reemplaza los story points de la historia indicada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Story points asignados"),
            @ApiResponse(responseCode = "400", description = "Story points ausentes o inválidos"),
            @ApiResponse(responseCode = "404", description = "La historia de usuario no existe")
    })
    @PatchMapping("/{id}/story-points")
    @PreAuthorize("hasAuthority('estimation:write')")
    public ResponseEntity<UserStoryResponse> assignStoryPoints(
            @PathVariable UUID id,
            @Valid @RequestBody StoryPointsRequest request
    ) {
        return ResponseEntity.ok(userStoryService.assignStoryPoints(id, request));
    }
}