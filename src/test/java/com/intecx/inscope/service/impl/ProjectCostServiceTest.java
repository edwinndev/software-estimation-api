package com.intecx.inscope.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intecx.inscope.entity.Project;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.ProjectCostMapper;
import com.intecx.inscope.repository.ProjectCostProjection;
import com.intecx.inscope.repository.ProjectCostRepository;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.service.support.CostAggregator;
import com.intecx.inscope.service.support.CostCalculator;
import com.intecx.inscope.service.support.ProfileCostAggregator;
import com.intecx.inscope.service.support.TaskCostHoursResolver;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectCostServiceTest {

    private static final UUID PROJECT_ID = new UUID(0, 1);
    private static final UUID TASK_ONE = new UUID(0, 2);
    private static final UUID TASK_TWO = new UUID(0, 3);
    private static final UUID PROFILE_ONE = new UUID(0, 4);
    private static final UUID PROFILE_TWO = new UUID(0, 5);

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final ProjectCostRepository costRepository = mock(ProjectCostRepository.class);
    private final CostAggregator costAggregator = new CostAggregator();
    private final ProjectCostServiceImpl service = new ProjectCostServiceImpl(
            projectRepository,
            costRepository,
            new CostCalculator(),
            costAggregator,
            new ProfileCostAggregator(costAggregator),
            new ProjectCostMapper(),
            new TaskCostHoursResolver()
    );

    @BeforeEach
    void givenActiveProject() {
        when(projectRepository.findByIdAndDeletedAtIsNull(PROJECT_ID)).thenReturn(Optional.of(new Project()));
    }

    @Test
    void calculatesSeveralTasksAndGroupsTheirExplicitContributionsByProfile() {
        List<ProjectCostProjection> rows = List.of(
                row(TASK_ONE, PROFILE_ONE, "2", "10", "PEN"),
                row(TASK_ONE, PROFILE_TWO, "3", "20", "PEN"),
                row(TASK_TWO, PROFILE_ONE, "1", "10", "PEN")
        );
        when(costRepository.findContributions(PROJECT_ID)).thenReturn(rows);

        var response = service.getCosts(PROJECT_ID);

        assertEquals(PROJECT_ID, response.projectId());
        assertEquals(0, new BigDecimal("90").compareTo(response.total().amount()));
        assertEquals("PEN", response.total().currency());
        assertEquals(2, response.tasks().size());
        assertEquals(2, response.profiles().size());
    }

    @Test
    void returnsZeroWithoutInventingCurrencyForEmptyProject() {
        when(costRepository.findContributions(PROJECT_ID)).thenReturn(List.of());

        var response = service.getCosts(PROJECT_ID);

        assertEquals(0, BigDecimal.ZERO.compareTo(response.total().amount()));
        assertNull(response.total().currency());
        assertTrue(response.tasks().isEmpty());
    }

    @Test
    void throwsWhenProjectIsMissing() {
        when(projectRepository.findByIdAndDeletedAtIsNull(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getCosts(PROJECT_ID));
    }

    private ProjectCostProjection row(UUID taskId, UUID profileId, String hours, String rate, String currency) {
        var row = mock(ProjectCostProjection.class);
        when(row.getTaskId()).thenReturn(taskId);
        when(row.getTechnicalProfileId()).thenReturn(profileId);
        when(row.getEstimateHours()).thenReturn(BigDecimal.TEN);
        when(row.getAdjustedHours()).thenReturn(hours == null ? null : new BigDecimal(hours));
        when(row.getHourlyRate()).thenReturn(rate == null ? null : new BigDecimal(rate));
        when(row.getCurrency()).thenReturn(currency);
        when(row.getProfileActive()).thenReturn(true);
        return row;
    }
}
