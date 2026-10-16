package com.intecx.inscope.repository;

import com.intecx.inscope.entity.UserStory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, UUID>, JpaSpecificationExecutor<UserStory> {

    @Query("SELECT s FROM UserStory s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<UserStory> findActiveById(@Param("id") UUID id);

    boolean existsByProject_IdAndCode(UUID projectId, String code);

    @Query("SELECT COALESCE(MAX(s.position), 0) FROM UserStory s "
            + "WHERE s.project.id = :projectId AND s.deletedAt IS NULL")
    int findLastPositionByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM UserStory s "
            + "WHERE s.project.id = :projectId AND s.deletedAt IS NULL")
    boolean existsActiveByProjectId(@Param("projectId") UUID projectId);
}