package com.intecx.inscope.repository;

import com.intecx.inscope.entity.RiskConfig;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskConfigRepository extends JpaRepository<RiskConfig, UUID> {

    Optional<RiskConfig> findByProjectId(UUID projectId);
}
