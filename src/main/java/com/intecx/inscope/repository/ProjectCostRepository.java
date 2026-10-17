package com.intecx.inscope.repository;

import com.intecx.inscope.entity.Project;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectCostRepository extends JpaRepository<Project, UUID> {

    @Query(value = """
            SELECT t.id AS "taskId", a.technical_profile_id AS "technicalProfileId",
                   CAST(t.estimate_hours AS NUMERIC) AS "estimateHours", h.hours AS "adjustedHours",
                   p.hourly_rate AS "hourlyRate", p.currency AS "currency",
                   p.is_active AS "profileActive", p.deleted_at AS "profileDeletedAt"
            FROM in_scope.tasks t
            JOIN in_scope.user_stories s ON s.id = t.story_id
            JOIN in_scope.projects project ON project.id = s.project_id
            LEFT JOIN in_scope.task_assignments a ON a.task_id = t.id
            LEFT JOIN in_scope.technical_profiles p ON p.id = a.technical_profile_id
            LEFT JOIN in_scope.task_hour_adjustments h
              ON h.task_id = a.task_id AND h.technical_profile_id = a.technical_profile_id
            WHERE project.id = :projectId AND project.deleted_at IS NULL
              AND s.deleted_at IS NULL AND t.deleted_at IS NULL
            ORDER BY s.position, s.id, t.position, t.id, a.technical_profile_id
            """, nativeQuery = true)
    List<ProjectCostProjection> findContributions(@Param("projectId") UUID projectId);
}
