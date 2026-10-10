package com.intecx.inscope.repository;

import com.intecx.inscope.entity.ReportSnapshot;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstimationSnapshotRepository extends JpaRepository<ReportSnapshot, UUID> {

    @Query(value = """
            SELECT id,
                   project_id AS "projectId",
                   base_effort_points AS "baseEffortPoints",
                   total_effort_hours AS "totalEffortHours",
                   total_time AS "totalTime",
                   time_unit::text AS "timeUnit",
                   total_cost AS "totalCost",
                   created_at AS "createdAt"
            FROM in_scope.report_snapshots
            WHERE project_id IN (:projectIds)
            ORDER BY created_at DESC, id
            """, nativeQuery = true)
    List<EstimationSnapshotProjection> findSummariesByProjectIds(@Param("projectIds") Collection<UUID> projectIds);

    @Query("""
            SELECT snapshot
            FROM ReportSnapshot snapshot
            JOIN FETCH snapshot.project project
            WHERE snapshot.id = :estimationId
              AND project.id = :projectId
              AND project.deletedAt IS NULL
            """)
    Optional<ReportSnapshot> findActiveByIdAndProjectId(
            @Param("estimationId") UUID estimationId,
            @Param("projectId") UUID projectId
    );
}
