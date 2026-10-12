package com.intecx.inscope.entity;

import com.intecx.inscope.enumeration.SprintUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "sprint_configs", schema = "in_scope")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class SprintConfig {

    // Mismos valores por defecto que sprint_configs en V1__schema.sql.
    private static final int DEFAULT_VELOCITY = 5;
    private static final int DEFAULT_DURATION = 2;
    private static final SprintUnit DEFAULT_UNIT = SprintUnit.DAYS;

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Column(nullable = false)
    private Integer velocity;

    @Column(nullable = false)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "in_scope.sprint_unit")
    private SprintUnit unit;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    public static SprintConfig withDefaults(UUID projectId) {
        SprintConfig config = new SprintConfig();
        config.setProjectId(projectId);
        config.setVelocity(DEFAULT_VELOCITY);
        config.setDuration(DEFAULT_DURATION);
        config.setUnit(DEFAULT_UNIT);
        return config;
    }
}