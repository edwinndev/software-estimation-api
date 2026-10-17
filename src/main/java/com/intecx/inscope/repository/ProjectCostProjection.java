package com.intecx.inscope.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ProjectCostProjection {
    UUID getTaskId();
    UUID getTechnicalProfileId();
    BigDecimal getEstimateHours();
    BigDecimal getAdjustedHours();
    BigDecimal getHourlyRate();
    String getCurrency();
    Boolean getProfileActive();
    LocalDateTime getProfileDeletedAt();
}
