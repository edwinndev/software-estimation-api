package com.intecx.inscope.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.inscope.dto.request.task.CreateTaskRequest;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.TaskMapper;
import com.intecx.inscope.mapper.TechnicalProfileMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.TechnicalProfileRepository;
import com.intecx.inscope.repository.UserStoryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TechnicalProfileRepository technicalProfileRepository;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(
                taskRepository,
                userStoryRepository,
                projectRepository,
                technicalProfileRepository,
                new TaskMapper(),
                new TechnicalProfileMapper()
        );
    }

    @Test
    void createsTaskWithExistingStoryAndResponsibleProfile() {
        UUID storyId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UserStory story = UserStory.builder()
                .id(storyId)
                .project(Project.builder().id(projectId).build())
                .build();
        TechnicalProfile profile = TechnicalProfile.builder()
                .id(profileId)
                .name("Ada Lovelace")
                .email("ada@example.com")
                .role(TechnicalRole.BACKEND)
                .experienceLevel(ExperienceLevel.SENIOR)
                .hourlyRate(BigDecimal.TEN)
                .build();
        when(userStoryRepository.findActiveById(storyId)).thenReturn(Optional.of(story));
        when(taskRepository.findLastPositionByStoryId(storyId)).thenReturn(0);
        when(technicalProfileRepository.findActiveByIds(anyCollection())).thenReturn(List.of(profile));
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = taskService.create(
                storyId,
                new CreateTaskRequest("Implementar API", "Crear el endpoint", 4, null, List.of(profileId)),
                UUID.randomUUID()
        );

        assertEquals(storyId, response.userStoryId());
        assertEquals(projectId, response.projectId());
        assertEquals(List.of(profileId), response.responsibleProfileIds());
    }

    @Test
    void rejectsTaskWhenStoryDoesNotExist() {
        UUID storyId = UUID.randomUUID();
        when(userStoryRepository.findActiveById(storyId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.create(
                storyId,
                new CreateTaskRequest("Implementar API", "Crear el endpoint", 4, null, List.of()),
                UUID.randomUUID()
        ));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void rejectsUnknownResponsibleProfilesWithoutSavingTask() {
        UUID storyId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UserStory story = UserStory.builder()
                .id(storyId)
                .project(Project.builder().id(UUID.randomUUID()).build())
                .build();
        when(userStoryRepository.findActiveById(storyId)).thenReturn(Optional.of(story));
        when(taskRepository.findLastPositionByStoryId(storyId)).thenReturn(0);
        when(technicalProfileRepository.findActiveByIds(anyCollection())).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> taskService.create(
                storyId,
                new CreateTaskRequest("Implementar API", "Crear el endpoint", 4, null, List.of(profileId)),
                UUID.randomUUID()
        ));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicateResponsibleProfiles() {
        UUID storyId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UserStory story = UserStory.builder()
                .id(storyId)
                .project(Project.builder().id(UUID.randomUUID()).build())
                .build();
        when(userStoryRepository.findActiveById(storyId)).thenReturn(Optional.of(story));
        when(taskRepository.findLastPositionByStoryId(storyId)).thenReturn(0);

        assertThrows(BadRequestException.class, () -> taskService.create(
                storyId,
                new CreateTaskRequest("Implementar API", "Crear el endpoint", 4, null, List.of(profileId, profileId)),
                UUID.randomUUID()
        ));
        verify(taskRepository, never()).save(any());
    }
}