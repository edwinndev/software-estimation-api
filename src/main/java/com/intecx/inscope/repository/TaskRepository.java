package com.intecx.inscope.repository;

import com.intecx.inscope.entity.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Task> findActiveById(@Param("id") UUID id);

    @Query("""
            select t from Task t
            join fetch t.story s
            where s.projectId = :projectId
              and t.deletedAt is null
              and s.deletedAt is null
            order by s.position, t.position
            """)
    List<Task> findActiveByProjectId(@Param("projectId") UUID projectId);

    @Query("""
            SELECT COUNT(t)
            FROM Task t
            WHERE t.story.projectId = :projectId
              AND t.deletedAt IS NULL
              AND t.story.deletedAt IS NULL
            """)
    long countActiveByProjectId(@Param("projectId") UUID projectId);
}