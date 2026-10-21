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
import com.intecx.inscope.entity.Task;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
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
import org.mockito.ArgumentCaptor;
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
        when(userStoryRepository.findByIdAndDeletedAtIsNull(storyId)).thenReturn(Optional.of(story));
        when(taskRepository.findLastPositionByStoryId(storyId)).thenReturn(0);
        when(technicalProfileRepository.findByIdInAndDeletedAtIsNullAndIsActiveTrue(anyCollection()))
                .thenReturn(List.of(profile));
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = taskService.create(
                storyId,
                CreateTaskRequest.builder()
                        .title("Implementar API")
                        .description("Crear el endpoint")
                        .estimateHours(4)
                        .responsibleProfileIds(List.of(profileId))
                        .build(),
                UUID.randomUUID()
        );

        assertEquals(storyId, response.userStoryId());
        assertEquals(projectId, response.projectId());
        assertEquals(List.of(profileId), response.responsibleProfileIds());
    }

    @Test
    void rejectsTaskWhenStoryDoesNotExist() {
        UUID storyId = UUID.randomUUID();
        when(userStoryRepository.findByIdAndDeletedAtIsNull(storyId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.create(
                storyId,
                CreateTaskRequest.builder()
                        .title("Implementar API")
                        .description("Crear el endpoint")
                        .estimateHours(4)
                        .responsibleProfileIds(List.of())
                        .build(),
                UUID.randomUUID()
        ));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void softDeletesTaskWithoutDeletingItsAssignments() {
        UUID taskId = UUID.randomUUID();
        Task task = Task.builder()
                .id(taskId)
                .story(UserStory.builder()
                        .id(UUID.randomUUID())
                        .project(Project.builder().id(UUID.randomUUID()).build())
                        .build())
                .title("Título anterior")
                .description("Descripción anterior")
                .estimateHours(4)
                .build();
        when(taskRepository.findByIdAndDeletedAtIsNull(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        taskService.delete(taskId, UUID.randomUUID());

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        org.junit.jupiter.api.Assertions.assertNotNull(taskCaptor.getValue().getDeletedAt());
    }
}
