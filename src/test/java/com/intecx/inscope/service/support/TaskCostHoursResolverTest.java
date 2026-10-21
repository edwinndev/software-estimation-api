package com.intecx.inscope.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.repository.ProjectCostProjection;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TaskCostHoursResolverTest {

    private static final UUID FIRST = new UUID(0, 1);
    private static final UUID SECOND = new UUID(0, 2);

    private final TaskCostHoursResolver resolver = new TaskCostHoursResolver();

    @Test
    void assignsAllHoursToOneProfile() {
        var hours = resolver.resolve(List.of(row(FIRST, "8", null)));

        assertEquals(0, new BigDecimal("8").compareTo(hours.get(FIRST)));
    }

    @Test
    void splitsExactlyWhenThereIsNoRemainder() {
        var hours = resolver.resolve(List.of(row(FIRST, "6", null), row(SECOND, "6", null)));

        assertEquals(0, new BigDecimal("3").compareTo(hours.get(FIRST)));
        assertEquals(0, new BigDecimal("3").compareTo(hours.get(SECOND)));
    }

    @Test
    void rejectsMissingOrNegativeEstimate() {
        assertThrows(BadRequestException.class, () -> resolver.resolve(List.of(row(FIRST, null, null))));
    }

    private ProjectCostProjection row(UUID profile, String estimate, String adjustment) {
        var row = mock(ProjectCostProjection.class);
        when(row.getTechnicalProfileId()).thenReturn(profile);
        when(row.getEstimateHours()).thenReturn(estimate == null ? null : new BigDecimal(estimate));
        when(row.getAdjustedHours()).thenReturn(adjustment == null ? null : new BigDecimal(adjustment));
        return row;
    }
}
