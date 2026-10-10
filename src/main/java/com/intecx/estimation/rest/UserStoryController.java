package com.intecx.estimation.rest;

import com.intecx.estimation.dto.request.StoryPointsRequest;
import com.intecx.estimation.dto.response.UserStoryResponse;
import com.intecx.estimation.service.UserStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-stories")
@RequiredArgsConstructor
@Tag(name = "Historias de usuario")
public class UserStoryController {

    private final UserStoryService userStoryService;

    @PutMapping("/{id}/story-points")
    @Operation(summary = "Asignar story points a una historia de usuario")
    public UserStoryResponse assignStoryPoints(
            @PathVariable UUID id,
            @Valid @RequestBody StoryPointsRequest request
    ) {
        return userStoryService.assignStoryPoints(id, request);
    }
}