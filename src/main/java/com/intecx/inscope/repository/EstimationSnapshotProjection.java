package com.intecx.inscope.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EstimationSnapshotProjection {

    UUID getId();

    UUID getProjectId();

    Integer getBaseEffortPoints();

    BigDecimal getTotalEffortHours();

    BigDecimal getTotalTime();

    String getTimeUnit();

    BigDecimal getTotalCost();

    LocalDateTime getCreatedAt();
}
