package com.intecx.estimation.service;

import com.intecx.estimation.common.PaginatedResponse;
import com.intecx.estimation.common.QueryFields;
import com.intecx.estimation.common.QueryRequest;
import com.intecx.estimation.common.QuerySupport;
import com.intecx.estimation.dto.request.UserStoryRequest;
import com.intecx.estimation.dto.request.UserStoryUpdateRequest;
import com.intecx.estimation.dto.response.UserStoryResponse;
import com.intecx.estimation.entity.UserStory;
import com.intecx.estimation.exception.ResourceNotFoundException;
import com.intecx.estimation.mapper.UserStoryMapper;
import com.intecx.estimation.repository.UserStoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStoryService {

    private static final QueryFields FIELDS = QueryFields.sortingBy("position")
            .filter("search", "title", "description", "code")
            .filter("priority", "priority")
            .filter("status", "status")
            .sortable("position", "code", "title", "priority", "status", "createdAt", "updatedAt");

    private final UserStoryRepository userStoryRepository;

    @Transactional(readOnly = true)
    public PaginatedResponse<UserStoryResponse> search(UUID projectId, QueryRequest request) {
        Specification<UserStory> projectScope = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("projectId"), projectId);
        return QuerySupport.search(
                userStoryRepository,
                projectScope,
                FIELDS,
                request,
                UserStoryMapper::toResponse,
                "userStoryResponse"
        );
    }

    @Transactional
    public UserStoryResponse create(UUID projectId, UserStoryRequest request) {
        List<UserStory> stories = userStoryRepository.findAllByProjectIdOrderByPositionAsc(projectId);
        UserStory story = new UserStory();
        story.setProjectId(projectId);
        story.setCode(nextCode(stories));
        story.setTitle(request.title().strip());
        story.setDescription(request.description().strip());
        story.setPriority(request.priority());
        story.setStatus(request.status());
        story.setPosition(nextPosition(stories));
        return UserStoryMapper.toResponse(userStoryRepository.save(story));
    }

    @Transactional
    public UserStoryResponse update(UUID storyId, UserStoryUpdateRequest request) {
        UserStory story = userStoryRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada"));
        story.setTitle(request.title().strip());
        story.setDescription(request.description().strip());
        story.setPriority(request.priority());
        story.setStatus(request.status());
        if (request.position() != null) {
            reorder(story, request.position());
        }
        return UserStoryMapper.toResponse(userStoryRepository.save(story));
    }

    @Transactional
    public void delete(UUID storyId) {
        UserStory story = userStoryRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada"));
        userStoryRepository.delete(story);
    }

    private void reorder(UserStory story, int requestedPosition) {
        List<UserStory> stories = new ArrayList<>(
                userStoryRepository.findAllByProjectIdOrderByPositionAsc(story.getProjectId()));
        stories.removeIf(item -> item.getId().equals(story.getId()));
        stories.add(Math.max(0, Math.min(requestedPosition, stories.size())), story);
        for (int position = 0; position < stories.size(); position++) {
            stories.get(position).setPosition(position);
        }
        userStoryRepository.saveAll(stories);
    }

    private int nextPosition(List<UserStory> stories) {
        return stories.stream().mapToInt(UserStory::getPosition).max().orElse(-1) + 1;
    }

    private String nextCode(List<UserStory> stories) {
        int next = stories.stream()
                .map(UserStory::getCode)
                .mapToInt(this::codeNumber)
                .max()
                .orElse(0) + 1;
        return "HU-%02d".formatted(next);
    }

    private int codeNumber(String code) {
        if (code == null || !code.startsWith("HU-")) {
            return 0;
        }
        try {
            return Integer.parseInt(code.substring(3));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
