package com.intecx.inscope.entity;

import com.intecx.inscope.enumeration.SprintUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sprint_configs", schema = "in_scope")
public class SprintConfig {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder.Default
    @Column(name = "velocity", nullable = false)
    private Integer velocity = 5;

    @Builder.Default
    @Column(name = "duration", nullable = false)
    private Integer duration = 2;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "unit", nullable = false, columnDefinition = "in_scope.sprint_unit")
    private SprintUnit unit = SprintUnit.DAYS;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;
}
