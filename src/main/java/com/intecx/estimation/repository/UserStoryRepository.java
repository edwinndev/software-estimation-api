package com.intecx.estimation.repository;

import com.intecx.estimation.entity.UserStory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserStoryRepository extends JpaRepository<UserStory, UUID>, JpaSpecificationExecutor<UserStory> {

    List<UserStory> findAllByProjectIdOrderByPositionAsc(UUID projectId);

    Optional<UserStory> findByIdAndProjectId(UUID id, UUID projectId);
}
