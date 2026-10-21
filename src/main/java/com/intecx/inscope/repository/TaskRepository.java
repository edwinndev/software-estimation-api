package com.intecx.inscope.repository;

import com.intecx.inscope.entity.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    Optional<Task> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByStory_IdAndDeletedAtIsNull(UUID storyId);

    List<Task> findByStory_Project_IdAndDeletedAtIsNull(UUID projectId);

    @Query("SELECT COALESCE(MAX(t.position), 0) FROM Task t "
            + "WHERE t.story.id = :storyId AND t.deletedAt IS NULL")
    int findLastPositionByStoryId(@Param("storyId") UUID storyId);
}
