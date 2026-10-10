package com.intecx.inscope.service;
import com.intecx.inscope.dto.request.estimation.StoryPointsRequest;
import com.intecx.inscope.dto.response.UserStoryResponse;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.UserStoryMapper;
import com.intecx.inscope.repository.UserStoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;

    @Transactional
    public UserStoryResponse assignStoryPoints(UUID id, StoryPointsRequest request) {
        UserStory story = userStoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia de usuario no encontrada"));
        // La columna es SMALLINT; el límite ya lo valida StoryPointsRequest.
        story.setStoryPoints(request.storyPoints().shortValue());
        return UserStoryMapper.toResponse(story);
    }
}