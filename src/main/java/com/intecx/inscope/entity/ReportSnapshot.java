package com.intecx.inscope.entity;

import com.intecx.inscope.enumeration.ProjectStatus;
import com.intecx.inscope.enumeration.RiskLevel;
import com.intecx.inscope.enumeration.SprintUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "report_snapshots", schema = "in_scope")
public class ReportSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "project_name", nullable = false, length = 150)
    private String projectName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "project_status", nullable = false, columnDefinition = "in_scope.project_status")
    private ProjectStatus projectStatus;

    @Column(name = "project_type", nullable = false, length = 40)
    private String projectType;

    @Column(name = "project_owner", nullable = false, length = 201)
    private String projectOwner;

    @Column(name = "base_effort_points", nullable = false)
    private Integer baseEffortPoints;

    @Column(name = "total_sprints", nullable = false)
    private Integer totalSprints;

    @Column(name = "total_effort_hours", nullable = false)
    private BigDecimal totalEffortHours;

    @Column(name = "stories_total", nullable = false)
    private Integer storiesTotal;

    @Column(name = "stories_with_points", nullable = false)
    private Integer storiesWithPoints;

    @Column(name = "tasks_total", nullable = false)
    private Integer tasksTotal;

    @Column(name = "tasks_with_hours", nullable = false)
    private Integer tasksWithHours;

    @Column(name = "base_time", nullable = false)
    private BigDecimal baseTime;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "time_unit", nullable = false, columnDefinition = "in_scope.sprint_unit")
    private SprintUnit timeUnit;

    @Column(name = "base_cost", nullable = false)
    private BigDecimal baseCost;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "risk_level", nullable = false, columnDefinition = "in_scope.risk_level")
    private RiskLevel riskLevel;

    @Column(name = "contingency_margin_percentage", nullable = false)
    private BigDecimal contingencyMarginPercentage;

    @Column(name = "contingency_time", nullable = false)
    private BigDecimal contingencyTime;

    @Column(name = "contingency_cost", nullable = false)
    private BigDecimal contingencyCost;

    @Column(name = "total_time", nullable = false)
    private BigDecimal totalTime;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private FileAttachment file;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private UUID createdBy;
}
