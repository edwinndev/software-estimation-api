package com.intecx.estimation.entity;

import com.intecx.estimation.enumeration.RiskLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_level_config", schema = "estimation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskLevelConfig {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, unique = true)
    private RiskLevel riskLevel;

    @Column(name = "contingency_percentage", nullable = false)
    private BigDecimal contingencyPercentage;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
