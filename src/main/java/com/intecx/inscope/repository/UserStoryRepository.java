package com.intecx.inscope.repository;

import com.intecx.inscope.entity.UserStory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, UUID>, JpaSpecificationExecutor<UserStory> {

    Optional<UserStory> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByProject_IdAndCode(UUID projectId, String code);

    boolean existsByProject_IdAndCodeAndIdNot(UUID projectId, String code, UUID id);

    boolean existsByProject_IdAndDeletedAtIsNull(UUID projectId);

    List<UserStory> findByProject_IdAndDeletedAtIsNull(UUID projectId);

    @Query("SELECT COALESCE(MAX(s.position), 0) FROM UserStory s "
            + "WHERE s.project.id = :projectId AND s.deletedAt IS NULL")
    int findLastPositionByProjectId(@Param("projectId") UUID projectId);
}
