package com.intecx.inscope.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.repository.ProjectCostProjection;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class TaskCostHoursResolverTest {

    private static final UUID FIRST = new UUID(0, 1);
    private static final UUID SECOND = new UUID(0, 2);
    private static final UUID THIRD = new UUID(0, 3);

    private final TaskCostHoursResolver resolver = new TaskCostHoursResolver();

    @Test
    void assignsAllHoursToOneProfile() {
        var hours = resolver.resolve(List.of(row(FIRST, "8", null)));

        assertHours("8", hours.get(FIRST));
    }

    @Test
    void splitsExactlyWhenThereIsNoRemainder() {
        var hours = resolver.resolve(List.of(row(FIRST, "6", null), row(SECOND, "6", null)));

        assertHours("3", hours.get(FIRST));
        assertHours("3", hours.get(SECOND));
    }

    @Test
    void preservesTotalAndAssignsRemainderToLastCanonicalUuid() {
        var hours = resolver.resolve(List.of(row(THIRD, "1", null), row(FIRST, "1", null), row(SECOND, "1", null)));

        assertEquals(List.of(FIRST, SECOND, THIRD), new ArrayList<>(hours.keySet()));
        assertHours("0.33", hours.get(FIRST));
        assertHours("0.33", hours.get(SECOND));
        assertHours("0.34", hours.get(THIRD));
        assertHours("1", hours.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void inputOrderDoesNotDetermineWhoReceivesRemainder() {
        var first = row(FIRST, "1", null);
        var second = row(SECOND, "1", null);
        var third = row(THIRD, "1", null);

        assertEquals(resolver.resolve(List.of(first, second, third)),
                resolver.resolve(List.of(third, first, second)));
    }

    @Test
    void sortsUuidByCanonicalTextRatherThanSignedLongComparison() {
        UUID largest = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        var hours = resolver.resolve(List.of(row(largest, "0.01", null), row(FIRST, "0.01", null)));

        assertHours("0", hours.get(FIRST));
        assertHours("0.01", hours.get(largest));
    }

    @Test
    void neverCreatesNegativeHoursWhenProfilesExceedAvailableHundredths() {
        var rows = IntStream.rangeClosed(1, 201)
                .mapToObj(index -> row(new UUID(0, index), "1", null)).toList();

        var hours = resolver.resolve(rows);

        assertTrue(hours.values().stream().allMatch(value -> value.signum() >= 0));
        assertHours("1", hours.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void adjustmentReplacesAutomaticHoursAndMayChangeTotalEffort() {
        var hours = resolver.resolve(List.of(row(FIRST, "6", "4"), row(SECOND, "6", null)));

        assertHours("4", hours.get(FIRST));
        assertHours("3", hours.get(SECOND));
        assertHours("7", hours.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void appliesSeveralAdjustmentsIncludingAnExplicitZero() {
        var hours = resolver.resolve(List.of(row(FIRST, "6", "0"), row(SECOND, "6", "8")));

        assertHours("0", hours.get(FIRST));
        assertHours("8", hours.get(SECOND));
    }

    @Test
    void manualHoursDoNotRedistributeAutomaticRemainder() {
        var hours = resolver.resolve(List.of(row(FIRST, "1", null), row(SECOND, "1", null), row(THIRD, "1", "2")));

        assertHours("0.33", hours.get(FIRST));
        assertHours("0.33", hours.get(SECOND));
        assertHours("2", hours.get(THIRD));
    }

    @Test
    void permitsZeroEstimate() {
        var hours = resolver.resolve(List.of(row(FIRST, "0", null), row(SECOND, "0", null)));

        assertTrue(hours.values().stream().allMatch(value -> value.signum() == 0));
    }

    @Test
    void rejectsMissingOrNegativeEstimate() {
        assertThrows(BadRequestException.class, () -> resolver.resolve(List.of(row(FIRST, null, null))));
        assertThrows(BadRequestException.class, () -> resolver.resolve(List.of(row(FIRST, "-1", null))));
    }

    @Test
    void rejectsUnrepresentableEstimateRatherThanSilentlyRoundingHours() {
        assertThrows(BadRequestException.class, () -> resolver.resolve(List.of(row(FIRST, "1.001", null))));
    }

    @Test
    void rejectsMissingAssignmentsOrProfile() {
        assertThrows(BadRequestException.class, () -> resolver.resolve(null));
        assertThrows(BadRequestException.class, () -> resolver.resolve(List.of()));
        assertThrows(BadRequestException.class, () -> resolver.resolve(List.of(row(null, "1", null))));
    }

    @Test
    void rejectsNegativeAdjustment() {
        assertThrows(BadRequestException.class, () -> resolver.resolve(List.of(row(FIRST, "1", "-0.01"))));
    }

    @Test
    void rejectsDuplicatedProfilesAndInconsistentTaskEstimates() {
        assertThrows(BadRequestException.class, () -> resolver.resolve(
                List.of(row(FIRST, "1", null), row(FIRST, "1", null))));
        assertThrows(BadRequestException.class, () -> resolver.resolve(
                List.of(row(FIRST, "1", null), row(SECOND, "2", null))));
    }

    @Test
    void consecutiveCalculationsDoNotShareState() {
        var first = resolver.resolve(List.of(row(FIRST, "1", null)));
        var second = resolver.resolve(List.of(row(FIRST, "2", null)));

        assertHours("1", first.get(FIRST));
        assertHours("2", second.get(FIRST));
    }

    private ProjectCostProjection row(UUID profile, String estimate, String adjustment) {
        var row = mock(ProjectCostProjection.class);
        when(row.getTechnicalProfileId()).thenReturn(profile);
        when(row.getEstimateHours()).thenReturn(estimate == null ? null : new BigDecimal(estimate));
        when(row.getAdjustedHours()).thenReturn(adjustment == null ? null : new BigDecimal(adjustment));
        return row;
    }

    private void assertHours(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
