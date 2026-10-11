package com.intecx.inscope.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intecx.inscope.dto.response.CostAmountResponse;
import com.intecx.inscope.dto.response.ProfileCostResponse;
import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProfileCostAggregatorTest {

    private static final UUID PROFILE_A = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID PROFILE_B = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final ProfileCostAggregator aggregator = new ProfileCostAggregator(new CostAggregator());

    @Test
    void acceptsSingleContribution() {
        assertProfileCosts(List.of(response(PROFILE_A, "100", "PEN")), aggregator.aggregate(List.of(contribution(PROFILE_A, "100", "PEN"))));
    }

    @Test
    void sumsContributionsForSameProfile() {
        assertProfileCosts(List.of(response(PROFILE_A, "150", "PEN")), aggregator.aggregate(List.of(contribution(PROFILE_A, "100", "PEN"), contribution(PROFILE_A, "50", "PEN"))));
    }

    @Test
    void aggregatesSeveralProfiles() {
        assertProfileCosts(List.of(response(PROFILE_A, "150", "PEN"), response(PROFILE_B, "80", "PEN")), aggregator.aggregate(List.of(contribution(PROFILE_A, "100", "PEN"), contribution(PROFILE_B, "80", "PEN"), contribution(PROFILE_A, "50", "PEN"))));
    }

    @Test
    void preservesFirstAppearanceOrder() {
        assertProfileCosts(List.of(response(PROFILE_B, "3", "PEN"), response(PROFILE_A, "5", "PEN")), aggregator.aggregate(List.of(contribution(PROFILE_B, "1", "PEN"), contribution(PROFILE_A, "5", "PEN"), contribution(PROFILE_B, "2", "PEN"))));
    }

    @Test
    void preservesExactDecimals() {
        assertProfileCosts(List.of(response(PROFILE_A, "0.123456790", "PEN")), aggregator.aggregate(List.of(contribution(PROFILE_A, "0.123456789", "PEN"), contribution(PROFILE_A, "0.000000001", "PEN"))));
    }

    @Test
    void acceptsZeroAmount() {
        assertProfileCosts(List.of(response(PROFILE_A, "0", "PEN")), aggregator.aggregate(List.of(contribution(PROFILE_A, "0", "PEN"))));
    }

    @Test
    void allowsDifferentCurrenciesAcrossProfiles() {
        assertProfileCosts(List.of(response(PROFILE_A, "100", "PEN"), response(PROFILE_B, "50", "USD")), aggregator.aggregate(List.of(contribution(PROFILE_A, "100", "PEN"), contribution(PROFILE_B, "50", "USD"))));
    }

    @Test
    void rejectsDifferentCurrenciesForSameProfile() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(List.of(contribution(PROFILE_A, "100", "PEN"), contribution(PROFILE_A, "20", "USD"))));
    }

    @Test
    void rejectsNullCollection() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(null));
    }

    @Test
    void rejectsEmptyCollection() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(List.of()));
    }

    @Test
    void rejectsNullContribution() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(Arrays.asList(contribution(PROFILE_A, "1", "PEN"), null)));
    }

    @Test
    void rejectsNullProfileId() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(List.of(contribution(null, "1", "PEN"))));
    }

    @Test
    void delegatesNullCostValidation() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(List.of(new ProfileCostContribution(PROFILE_A, null))));
    }

    @Test
    void delegatesNullAmountValidation() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(List.of(new ProfileCostContribution(PROFILE_A, new CostAmountResponse(null, "PEN")))));
    }

    @Test
    void delegatesNegativeAmountValidation() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(List.of(contribution(PROFILE_A, "-0.01", "PEN"))));
    }

    @Test
    void delegatesNullCurrencyValidation() {
        assertThrows(BadRequestException.class, () -> aggregator.aggregate(List.of(contribution(PROFILE_A, "1", null))));
    }

    @Test
    void delegatesInvalidCurrencyValidation() {
        for (String currency : List.of("", " ", "pen", "PE", "P3N")) {
            assertThrows(BadRequestException.class, () -> aggregator.aggregate(List.of(contribution(PROFILE_A, "1", currency))));
        }
    }

    @Test
    void keepsConsecutiveCallsIndependent() {
        assertProfileCosts(List.of(response(PROFILE_A, "1", "PEN")), aggregator.aggregate(List.of(contribution(PROFILE_A, "1", "PEN"))));
        assertProfileCosts(List.of(response(PROFILE_B, "2", "USD")), aggregator.aggregate(List.of(contribution(PROFILE_B, "2", "USD"))));
    }

    private void assertProfileCosts(List<ProfileCostResponse> expected, List<ProfileCostResponse> actual) {
        assertEquals(expected.size(), actual.size());
        for (int index = 0; index < expected.size(); index++) {
            ProfileCostResponse expectedProfile = expected.get(index);
            ProfileCostResponse actualProfile = actual.get(index);
            assertEquals(expectedProfile.technicalProfileId(), actualProfile.technicalProfileId());
            assertEquals(expectedProfile.subtotal().currency(), actualProfile.subtotal().currency());
            assertEquals(0, expectedProfile.subtotal().amount().compareTo(actualProfile.subtotal().amount()));
        }
    }
    private ProfileCostContribution contribution(UUID profileId, String amount, String currency) {
        return new ProfileCostContribution(profileId, new CostAmountResponse(new BigDecimal(amount), currency));
    }

    private ProfileCostResponse response(UUID profileId, String amount, String currency) {
        return new ProfileCostResponse(profileId, new CostAmountResponse(new BigDecimal(amount), currency));
    }
}
