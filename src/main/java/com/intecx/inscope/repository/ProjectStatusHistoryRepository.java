package com.intecx.inscope.repository;

import com.intecx.inscope.entity.ProjectStatusHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectStatusHistoryRepository extends JpaRepository<ProjectStatusHistory, UUID> {

    @Query("""
            SELECT history
            FROM ProjectStatusHistory history
            JOIN FETCH history.changedBy
            WHERE history.project.id = :projectId
            ORDER BY history.changedAt DESC, history.id DESC
            """)
    List<ProjectStatusHistory> findByProjectIdOrderByChangedAtDesc(@Param("projectId") UUID projectId);
}
