package com.intecx.estimation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.estimation.dto.request.UserStoryRequest;
import com.intecx.estimation.dto.request.UserStoryUpdateRequest;
import com.intecx.estimation.entity.UserStory;
import com.intecx.estimation.enumeration.UserStoryPriority;
import com.intecx.estimation.enumeration.UserStoryStatus;
import com.intecx.estimation.exception.ResourceNotFoundException;
import com.intecx.estimation.repository.UserStoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserStoryServiceTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @InjectMocks
    private UserStoryService userStoryService;

    @Test
    void createsStoryWithNextCodeAndPosition() {
        UUID projectId = UUID.randomUUID();
        when(userStoryRepository.findAllByProjectIdOrderByPositionAsc(projectId)).thenReturn(List.of());
        when(userStoryRepository.save(any(UserStory.class))).thenAnswer(invocation -> {
            UserStory story = invocation.getArgument(0);
            story.setId(UUID.randomUUID());
            return story;
        });

        var response = userStoryService.create(
                projectId,
                new UserStoryRequest("  Sign in  ", "A detailed user story", UserStoryPriority.HIGH,
                        UserStoryStatus.READY)
        );

        assertEquals("HU-01", response.code());
        assertEquals(0, response.position());
        assertEquals("Sign in", response.title());
        assertEquals(projectId, response.projectId());
    }

    @Test
    void movesStoryAndRenumbersItsSiblings() {
        UUID projectId = UUID.randomUUID();
        UserStory first = story(UUID.randomUUID(), projectId, 0);
        UserStory second = story(UUID.randomUUID(), projectId, 1);
        when(userStoryRepository.findById(second.getId())).thenReturn(Optional.of(second));
        when(userStoryRepository.findAllByProjectIdOrderByPositionAsc(projectId))
                .thenReturn(List.of(first, second));
        when(userStoryRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userStoryRepository.save(second)).thenReturn(second);

        var response = userStoryService.update(
                second.getId(),
                new UserStoryUpdateRequest("Updated", "A detailed user story", UserStoryPriority.MEDIUM,
                        UserStoryStatus.DRAFT, 0)
        );

        assertEquals(0, response.position());
        assertEquals(1, first.getPosition());
        verify(userStoryRepository).saveAll(any());
    }

    @Test
    void rejectsDeletingUnknownStory() {
        UUID id = UUID.randomUUID();
        when(userStoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userStoryService.delete(id));
    }

    private UserStory story(UUID id, UUID projectId, int position) {
        UserStory story = new UserStory();
        story.setId(id);
        story.setProjectId(projectId);
        story.setPosition(position);
        return story;
    }
}
