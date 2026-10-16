package com.intecx.inscope.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.inscope.dto.request.userstory.CreateUserStoryRequest;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.UserStoryMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.UserStoryRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserStoryServiceImplTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    private UserStoryServiceImpl userStoryService;

    @BeforeEach
    void setUp() {
        userStoryService = new UserStoryServiceImpl(
                userStoryRepository,
                projectRepository,
                taskRepository,
                new UserStoryMapper()
        );
    }

    @Test
    void createsStoryForActiveProject() {
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).build();
        when(projectRepository.findActiveById(projectId)).thenReturn(Optional.of(project));
        when(userStoryRepository.existsByProject_IdAndCode(projectId, "US-1")).thenReturn(false);
        when(userStoryRepository.findLastPositionByProjectId(projectId)).thenReturn(2);
        when(userStoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userStoryService.create(
                projectId,
                new CreateUserStoryRequest("US-1", "Acceso", "Permitir acceso", null, null, null),
                UUID.randomUUID()
        );

        assertEquals(projectId, response.projectId());
        assertEquals("US-1", response.code());
        assertEquals(3, response.position());
    }

    @Test
    void rejectsStoryForMissingProject() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findActiveById(projectId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userStoryService.create(
                projectId,
                new CreateUserStoryRequest("US-1", "Acceso", "Permitir acceso", null, null, null),
                UUID.randomUUID()
        ));
        verify(userStoryRepository, never()).save(any());
    }

    @Test
    void rejectsDeletingStoryWithActiveTasks() {
        UUID storyId = UUID.randomUUID();
        when(userStoryRepository.findActiveById(storyId))
                .thenReturn(Optional.of(com.intecx.inscope.entity.UserStory.builder().id(storyId).build()));
        when(taskRepository.existsActiveByStoryId(storyId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userStoryService.delete(storyId, UUID.randomUUID()));
        verify(userStoryRepository, never()).save(any());
    }
}