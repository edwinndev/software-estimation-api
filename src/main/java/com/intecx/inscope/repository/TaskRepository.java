package com.intecx.inscope.repository;

import com.intecx.inscope.entity.Task;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Task> findActiveById(@Param("id") UUID id);

    @Query("SELECT COALESCE(MAX(t.position), 0) FROM Task t "
            + "WHERE t.story.id = :storyId AND t.deletedAt IS NULL")
    int findLastPositionByStoryId(@Param("storyId") UUID storyId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Task t "
            + "WHERE t.story.id = :storyId AND t.deletedAt IS NULL")
    boolean existsActiveByStoryId(@Param("storyId") UUID storyId);
}