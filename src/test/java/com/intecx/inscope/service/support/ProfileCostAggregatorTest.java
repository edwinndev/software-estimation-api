package com.intecx.inscope.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intecx.inscope.dto.response.CostAmountResponse;
import com.intecx.inscope.dto.response.ProfileCostResponse;
import com.intecx.inscope.dto.response.TaskProfileCostResponse;
import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileCostAggregatorTest {

    private static final UUID PROFILE_A = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID PROFILE_B = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final ProfileCostAggregator aggregator = new ProfileCostAggregator(new CostAggregator());

    @Test
    void sumsContributionsForSameProfile() {
        List<ProfileCostResponse> result = aggregator.aggregate(List.of(
                contribution(PROFILE_A, "100", "PEN"),
                contribution(PROFILE_A, "50", "PEN")
        ));

        assertEquals(1, result.size());
        assertEquals(PROFILE_A, result.getFirst().technicalProfileId());
        assertEquals(0, new BigDecimal("150").compareTo(result.getFirst().subtotal().amount()));
    }

    @Test
    void aggregatesSeveralProfiles() {
        List<ProfileCostResponse> result = aggregator.aggregate(List.of(
                contribution(PROFILE_A, "100", "PEN"),
                contribution(PROFILE_B, "80", "PEN"),
                contribution(PROFILE_A, "50", "PEN")
        ));

        assertEquals(2, result.size());
        assertEquals(PROFILE_A, result.getFirst().technicalProfileId());
        assertEquals(PROFILE_B, result.get(1).technicalProfileId());
    }

    @Test
    void rejectsEmptyCollection() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(List.of()));
    }

    private TaskProfileCostResponse contribution(UUID profileId, String amount, String currency) {
        return TaskProfileCostResponse.builder()
                .technicalProfileId(profileId)
                .hours(BigDecimal.ONE)
                .hourlyRate(BigDecimal.ONE)
                .cost(new CostAmountResponse(new BigDecimal(amount), currency))
                .build();
    }
}
