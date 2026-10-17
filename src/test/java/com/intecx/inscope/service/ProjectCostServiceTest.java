package com.intecx.inscope.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.intecx.inscope.entity.Project;
import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.ProjectCostMapper;
import com.intecx.inscope.repository.ProjectCostProjection;
import com.intecx.inscope.repository.ProjectCostRepository;
import com.intecx.inscope.repository.ProjectRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProjectCostServiceTest {

    private static final UUID PROJECT_ID = new UUID(0, 1);
    private static final UUID TASK_ONE = new UUID(0, 2);
    private static final UUID TASK_TWO = new UUID(0, 3);
    private static final UUID PROFILE_ONE = new UUID(0, 4);
    private static final UUID PROFILE_TWO = new UUID(0, 5);

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final ProjectCostRepository costRepository = mock(ProjectCostRepository.class);
    private final CostAggregator costAggregator = new CostAggregator();
    private final ProjectCostService service = new ProjectCostService(
            projectRepository, costRepository, new CostCalculator(), costAggregator,
            new ProfileCostAggregator(costAggregator), new ProjectCostMapper(), new TaskCostHoursResolver());

    @BeforeEach
    void givenActiveProject() {
        when(projectRepository.findActiveById(PROJECT_ID)).thenReturn(Optional.of(new Project()));
    }

    @Test
    void calculatesSeveralTasksAndGroupsTheirExplicitContributionsByProfile() {
        givenRows(List.of(
                row(TASK_ONE, PROFILE_ONE, "2", "10", "PEN"),
                row(TASK_ONE, PROFILE_TWO, "3", "20", "PEN"),
                row(TASK_TWO, PROFILE_ONE, "1", "10", "PEN")));

        var response = service.getCosts(PROJECT_ID);

        assertEquals(PROJECT_ID, response.getProjectId());
        assertMoney("90", response.getTotal().amount());
        assertEquals("PEN", response.getTotal().currency());
        assertEquals(2, response.getTasks().size());
        assertEquals(2, response.getProfiles().size());
        assertMoney("80", response.getTasks().getFirst().getTotal().amount());
        assertMoney("10", response.getTasks().get(1).getTotal().amount());
        assertEquals(TASK_ONE, response.getTasks().getFirst().getTaskId());
        assertEquals(2, response.getTasks().getFirst().getContributions().size());
        assertEquals(PROFILE_ONE, response.getProfiles().getFirst().technicalProfileId());
        assertMoney("30", response.getProfiles().getFirst().subtotal().amount());
        assertMoney("60", response.getProfiles().get(1).subtotal().amount());
        assertMoney("20", response.getTasks().getFirst().getContributions().getFirst().getCost().amount());
    }

    @Test
    void preservesFractionalAmountsWithoutRoundingContributions() {
        givenRows(List.of(
                row(TASK_ONE, PROFILE_ONE, "0.01", "0.50", "PEN"),
                row(TASK_TWO, PROFILE_ONE, "0.01", "0.50", "PEN")));

        var response = service.getCosts(PROJECT_ID);

        assertMoney("0.005", response.getTasks().getFirst().getContributions().getFirst().getCost().amount());
        assertMoney("0.010", response.getTotal().amount());
        assertMoney("0.010", response.getProfiles().getFirst().subtotal().amount());
    }

    @Test
    void acceptsExplicitZeroHours() {
        givenRows(
                List.of(row(TASK_ONE, PROFILE_ONE, "0", "10", "USD")));

        var response = service.getCosts(PROJECT_ID);

        assertMoney("0", response.getTotal().amount());
        assertEquals("USD", response.getTotal().currency());
        assertEquals(1, response.getTasks().size());
    }

    @Test
    void recalculatesCurrentHoursAndCerOnConsecutiveReadsWithoutAccumulating() {
        var initial = List.of(row(TASK_ONE, PROFILE_ONE, "2", "10", "PEN"));
        var hoursChanged = List.of(row(TASK_ONE, PROFILE_ONE, "3", "10", "PEN"));
        var cerChanged = List.of(row(TASK_ONE, PROFILE_ONE, "3", "20", "PEN"));
        when(costRepository.findContributions(PROJECT_ID))
                .thenReturn(initial)
                .thenReturn(hoursChanged)
                .thenReturn(cerChanged);

        var first = service.getCosts(PROJECT_ID);
        var second = service.getCosts(PROJECT_ID);
        var third = service.getCosts(PROJECT_ID);

        assertMoney("20", first.getTotal().amount());
        assertMoney("30", second.getTotal().amount());
        assertMoney("60", third.getTotal().amount());
        assertMoney("20", first.getTotal().amount());
    }

    @Test
    void keepsDifferentProjectsAndCurrenciesIndependent() {
        UUID otherProject = new UUID(0, 6);
        when(projectRepository.findActiveById(otherProject)).thenReturn(Optional.of(new Project()));
        givenRows(
                List.of(row(TASK_ONE, PROFILE_ONE, "1", "10", "PEN")));
        var otherRows = List.of(row(TASK_TWO, PROFILE_TWO, "2", "30", "USD"));
        when(costRepository.findContributions(otherProject)).thenReturn(otherRows);

        assertMoney("10", service.getCosts(PROJECT_ID).getTotal().amount());
        var other = service.getCosts(otherProject);
        assertMoney("60", other.getTotal().amount());
        assertEquals("USD", other.getTotal().currency());
    }

    @Test
    void calculatesAutomaticHoursForOneProfile() {
        var contribution = row(TASK_ONE, PROFILE_ONE, null, "10", "PEN");
        when(contribution.getEstimateHours()).thenReturn(BigDecimal.ONE);
        givenRows(List.of(contribution));

        var response = service.getCosts(PROJECT_ID);

        assertMoney("10", response.getTotal().amount());
        assertMoney("1", response.getTasks().getFirst().getContributions().getFirst().getHours());
    }

    @Test
    void adjustmentReplacesOnlyItsAutomaticShareWithoutRedistributingOtherShares() {
        givenRows(List.of(
                row(TASK_ONE, PROFILE_ONE, "2", "10", "PEN"),
                row(TASK_ONE, PROFILE_TWO, null, "20", "PEN")));

        var response = service.getCosts(PROJECT_ID);

        assertMoney("120", response.getTotal().amount());
        assertMoney("2", response.getTasks().getFirst().getContributions().getFirst().getHours());
        assertMoney("5", response.getTasks().getFirst().getContributions().get(1).getHours());
    }

    @Test
    void distributesResidueDeterministicallyBeforeApplyingDifferentCerValues() {
        UUID thirdProfile = new UUID(0, 7);
        var first = row(TASK_ONE, PROFILE_ONE, null, "10", "PEN");
        var second = row(TASK_ONE, PROFILE_TWO, null, "20", "PEN");
        var third = row(TASK_ONE, thirdProfile, null, "30", "PEN");
        for (var contribution : List.of(first, second, third)) {
            when(contribution.getEstimateHours()).thenReturn(BigDecimal.ONE);
        }
        givenRows(List.of(third, first, second));

        var response = service.getCosts(PROJECT_ID);
        var contributions = response.getTasks().getFirst().getContributions();

        assertMoney("20.10", response.getTotal().amount());
        assertEquals(PROFILE_ONE, contributions.getFirst().getTechnicalProfileId());
        assertMoney("0.33", contributions.getFirst().getHours());
        assertEquals(thirdProfile, contributions.getLast().getTechnicalProfileId());
        assertMoney("0.34", contributions.getLast().getHours());
    }

    @Test
    void recalculatesWhenBaseEstimateChangesWithoutAdjustments() {
        var initial = row(TASK_ONE, PROFILE_ONE, null, "10", "PEN");
        var changed = row(TASK_ONE, PROFILE_ONE, null, "10", "PEN");
        when(initial.getEstimateHours()).thenReturn(new BigDecimal("2"));
        when(changed.getEstimateHours()).thenReturn(new BigDecimal("3"));
        when(costRepository.findContributions(PROJECT_ID))
                .thenReturn(List.of(initial)).thenReturn(List.of(changed));

        assertMoney("20", service.getCosts(PROJECT_ID).getTotal().amount());
        assertMoney("30", service.getCosts(PROJECT_ID).getTotal().amount());
    }

    @Test
    void returnsZeroWithoutInventingCurrencyForEmptyProject() {
        givenRows(List.of());

        var response = service.getCosts(PROJECT_ID);

        assertMoney("0", response.getTotal().amount());
        assertNull(response.getTotal().currency());
        assertTrue(response.getTasks().isEmpty());
        assertTrue(response.getProfiles().isEmpty());
    }

    @Test
    void rejectsWholeProjectInsteadOfSkippingAnInvalidTask() {
        givenRows(List.of(row(TASK_ONE, PROFILE_ONE, null, "10", "PEN"),
                row(TASK_TWO, PROFILE_TWO, "1", "0", "PEN")));

        assertThrows(BadRequestException.class, () -> service.getCosts(PROJECT_ID));
    }

    @Test
    void rejectsTaskWithoutAssignedProfile() {
        givenRows(
                List.of(row(TASK_ONE, null, "1", "10", "PEN")));

        assertThrows(BadRequestException.class, () -> service.getCosts(PROJECT_ID));
    }

    @Test
    void rejectsInactiveProfile() {
        var contribution = row(TASK_ONE, PROFILE_ONE, "1", "10", "PEN");
        when(contribution.getProfileActive()).thenReturn(false);
        givenRows(List.of(contribution));

        assertThrows(ConflictException.class, () -> service.getCosts(PROJECT_ID));
    }

    @Test
    void rejectsMissingJoinedProfile() {
        var contribution = row(TASK_ONE, PROFILE_ONE, "1", null, null);
        when(contribution.getProfileActive()).thenReturn(null);
        givenRows(List.of(contribution));

        assertThrows(ConflictException.class, () -> service.getCosts(PROJECT_ID));
    }

    @Test
    void rejectsDeletedProfile() {
        var contribution = row(TASK_ONE, PROFILE_ONE, "1", "10", "PEN");
        when(contribution.getProfileDeletedAt()).thenReturn(LocalDateTime.of(2026, 1, 1, 0, 0));
        givenRows(List.of(contribution));

        assertThrows(ConflictException.class, () -> service.getCosts(PROJECT_ID));
    }

    @ParameterizedTest
    @MethodSource("invalidCostInputs")
    void delegatesInvalidHoursAndCerToPublishedCalculator(String hours, String rate) {
        givenRows(
                List.of(row(TASK_ONE, PROFILE_ONE, hours, rate, "PEN")));

        assertThrows(BadRequestException.class, () -> service.getCosts(PROJECT_ID));
    }

    static Stream<Arguments> invalidCostInputs() {
        return Stream.of(
                Arguments.of("-0.01", "10"),
                Arguments.of("1", null),
                Arguments.of("1", "0"),
                Arguments.of("1", "-1"),
                Arguments.of("1", "10000000000.00"),
                Arguments.of("1", "0.001"),
                Arguments.of("1", "10.001"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"pen", "PE", "PENN", " PEN "})
    void delegatesInvalidCurrencyToPublishedAggregator(String currency) {
        givenRows(
                List.of(row(TASK_ONE, PROFILE_ONE, "1", "10", currency)));

        assertThrows(BadRequestException.class, () -> service.getCosts(PROJECT_ID));
    }

    @Test
    void rejectsDifferentCurrenciesWithoutConversion() {
        givenRows(List.of(
                row(TASK_ONE, PROFILE_ONE, "1", "10", "PEN"),
                row(TASK_TWO, PROFILE_TWO, "1", "10", "USD")));

        assertThrows(BadRequestException.class, () -> service.getCosts(PROJECT_ID));
    }

    @Test
    void rejectsProjectMissingFromActiveLookupBeforeReadingContributions() {
        when(projectRepository.findActiveById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getCosts(PROJECT_ID));
        verifyNoInteractions(costRepository);
    }

    @ParameterizedTest
    @MethodSource("invalidBaseEstimates")
    void rejectsAbsentOrInvalidBaseEstimate(BigDecimal estimate) {
        var contribution = row(TASK_ONE, PROFILE_ONE, "1", "10", "PEN");
        when(contribution.getEstimateHours()).thenReturn(estimate);
        givenRows(List.of(contribution));

        assertThrows(BadRequestException.class, () -> service.getCosts(PROJECT_ID));
    }

    static Stream<BigDecimal> invalidBaseEstimates() {
        return Stream.of(null, BigDecimal.ONE.negate());
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

    private void givenRows(List<ProjectCostProjection> rows) {
        when(costRepository.findContributions(PROJECT_ID)).thenReturn(rows);
    }

    private void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
