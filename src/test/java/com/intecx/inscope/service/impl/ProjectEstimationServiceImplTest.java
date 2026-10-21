package com.intecx.inscope.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.intecx.inscope.dto.request.project.ProjectContingencyRequest;
import com.intecx.inscope.dto.response.ProjectContingencyResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.RiskConfig;
import com.intecx.inscope.enumeration.RiskLevel;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.ReportSnapshotMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.ReportSnapshotRepository;
import com.intecx.inscope.repository.RiskConfigRepository;
import com.intecx.inscope.repository.SprintConfigRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.UserStoryRepository;
import com.intecx.inscope.service.ProjectCostService;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectEstimationServiceImplTest {

    private static final UUID PROJECT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserStoryRepository userStoryRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private SprintConfigRepository sprintConfigRepository;
    @Mock
    private RiskConfigRepository riskConfigRepository;
    @Mock
    private ReportSnapshotRepository reportSnapshotRepository;
    @Mock
    private ProjectCostService projectCostService;
    @Mock
    private ReportSnapshotMapper reportSnapshotMapper;

    private ProjectEstimationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProjectEstimationServiceImpl(
                projectRepository,
                userStoryRepository,
                taskRepository,
                sprintConfigRepository,
                riskConfigRepository,
                reportSnapshotRepository,
                projectCostService,
                reportSnapshotMapper
        );
    }

    @Test
    void calculateAppliesContingencyMargin() {
        when(projectRepository.findByIdAndDeletedAtIsNull(PROJECT_ID))
                .thenReturn(Optional.of(new Project()));
        when(riskConfigRepository.findByProjectId(PROJECT_ID))
                .thenReturn(Optional.of(RiskConfig.builder()
                        .level(RiskLevel.MEDIUM)
                        .contingencyMargin(new BigDecimal("10"))
                        .build()));

        ProjectContingencyRequest request = ProjectContingencyRequest.builder()
                .estimatedTime(new BigDecimal("100.00"))
                .estimatedCost(new BigDecimal("1000.00"))
                .build();

        ProjectContingencyResponse response = service.calculate(PROJECT_ID, request);

        assertEquals(RiskLevel.MEDIUM, response.riskLevel());
        assertEquals(new BigDecimal("10"), response.contingencyMarginPercentage());
        assertEquals(new BigDecimal("110.00"), response.totalTime());
        assertEquals(new BigDecimal("1100.00"), response.totalCost());
    }

    @Test
    void calculateThrowsWhenProjectIsMissing() {
        when(projectRepository.findByIdAndDeletedAtIsNull(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.calculate(
                PROJECT_ID,
                ProjectContingencyRequest.builder()
                        .estimatedTime(BigDecimal.ONE)
                        .estimatedCost(BigDecimal.ONE)
                        .build()
        ));
    }
}
