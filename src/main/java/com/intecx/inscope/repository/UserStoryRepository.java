package com.intecx.inscope.repository;

import com.intecx.inscope.entity.UserStory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserStoryRepository extends JpaRepository<UserStory, UUID> {

    @Query("SELECT s FROM UserStory s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<UserStory> findActiveById(@Param("id") UUID id);

    @Query("""
            SELECT COALESCE(SUM(s.storyPoints), 0L)
            FROM UserStory s
            WHERE s.projectId = :projectId AND s.deletedAt IS NULL
            """)
    long sumActiveStoryPointsByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT COUNT(s) FROM UserStory s WHERE s.projectId = :projectId AND s.deletedAt IS NULL")
    long countActiveByProjectId(@Param("projectId") UUID projectId);

    @Query("""
            SELECT COUNT(s)
            FROM UserStory s
            WHERE s.projectId = :projectId AND s.deletedAt IS NULL AND s.storyPoints IS NOT NULL
            """)
    long countActiveWithStoryPointsByProjectId(@Param("projectId") UUID projectId);
}