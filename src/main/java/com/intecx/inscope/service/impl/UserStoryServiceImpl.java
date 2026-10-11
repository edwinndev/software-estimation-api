package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.request.estimation.StoryPointsRequest;
import com.intecx.inscope.dto.response.UserStoryResponse;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.UserStoryMapper;
import com.intecx.inscope.repository.UserStoryRepository;
import com.intecx.inscope.service.UserStoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStoryServiceImpl implements UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final UserStoryMapper userStoryMapper;

    @Override
    @Transactional
    public UserStoryResponse assignStoryPoints(UUID id, StoryPointsRequest request) {
        UserStory story = userStoryRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia de usuario no encontrada"));
        // La columna es SMALLINT; el límite ya lo valida StoryPointsRequest.
        story.setStoryPoints(request.storyPoints().shortValue());
        return userStoryMapper.toResponse(story);
    }
}