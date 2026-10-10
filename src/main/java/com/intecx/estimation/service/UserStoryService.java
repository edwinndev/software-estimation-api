package com.intecx.estimation.service;

import com.intecx.estimation.dto.request.StoryPointsRequest;
import com.intecx.estimation.dto.response.UserStoryResponse;
import com.intecx.estimation.entity.UserStory;
import com.intecx.estimation.exception.ResourceNotFoundException;
import com.intecx.estimation.mapper.UserStoryMapper;
import com.intecx.estimation.repository.UserStoryRepository;
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
        story.setStoryPoints(request.storyPoints().shortValue());
        return UserStoryMapper.toResponse(story);
    }
}