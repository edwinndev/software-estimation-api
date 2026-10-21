package com.intecx.inscope.repository;

import com.intecx.inscope.entity.SprintConfig;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintConfigRepository extends JpaRepository<SprintConfig, UUID> {

    Optional<SprintConfig> findByProjectId(UUID projectId);
}
