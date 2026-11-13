package com.intecx.estimation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.estimation.dto.request.TaskResponsiblesRequest;
import com.intecx.estimation.dto.request.TechnicalTaskRequest;
import com.intecx.estimation.entity.TechnicalTask;
import com.intecx.estimation.enumeration.TaskStatus;
import com.intecx.estimation.exception.ResourceNotFoundException;
import com.intecx.estimation.repository.TechnicalTaskRepository;
import com.intecx.estimation.repository.UserStoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TechnicalTaskServiceTest {

    @Mock
    private TechnicalTaskRepository technicalTaskRepository;

    @Mock
    private UserStoryRepository userStoryRepository;

    @InjectMocks
    private TechnicalTaskService technicalTaskService;

    @Test
    void createsTaskUnderAnExistingStory() {
        UUID storyId = UUID.randomUUID();
        when(userStoryRepository.existsById(storyId)).thenReturn(true);
        when(technicalTaskRepository.findAllByStoryIdOrderByPositionAsc(storyId)).thenReturn(List.of());
        when(technicalTaskRepository.save(any(TechnicalTask.class))).thenAnswer(invocation -> {
            TechnicalTask task = invocation.getArgument(0);
            task.setId(UUID.randomUUID());
            return task;
        });

        var response = technicalTaskService.create(
                new TechnicalTaskRequest(storyId, "Build endpoint", "Implement the backend endpoint",
                        4, TaskStatus.TODO)
        );

        assertEquals(storyId, response.storyId());
        assertEquals(0, response.position());
        assertEquals(4, response.estimateHours());
    }

    @Test
    void rejectsCreatingTaskForUnknownStory() {
        UUID storyId = UUID.randomUUID();
        when(userStoryRepository.existsById(storyId)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> technicalTaskService.create(
                        new TechnicalTaskRequest(storyId, "Build endpoint", "Implement the backend endpoint",
                                4, TaskStatus.TODO)
                )
        );
    }

    @Test
    void replacesTaskResponsiblesAndAllowsClearingThem() {
        UUID taskId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        TechnicalTask task = new TechnicalTask();
        task.setId(taskId);
        when(technicalTaskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(technicalTaskRepository.save(task)).thenReturn(task);

        var response = technicalTaskService.updateResponsibles(
                taskId,
                new TaskResponsiblesRequest(List.of(profileId))
        );
        assertEquals(List.of(profileId), response.profileIds());

        response = technicalTaskService.updateResponsibles(taskId, new TaskResponsiblesRequest(List.of()));
        assertEquals(List.of(), response.profileIds());
        verify(technicalTaskRepository, org.mockito.Mockito.times(2)).save(task);
    }
}
