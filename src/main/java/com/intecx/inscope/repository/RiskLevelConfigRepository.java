package com.intecx.inscope.repository;

import com.intecx.inscope.entity.RiskLevelConfig;
import com.intecx.inscope.enumeration.RiskLevel;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskLevelConfigRepository extends JpaRepository<RiskLevelConfig, UUID> {

    @Query("SELECT c FROM RiskLevelConfig c WHERE c.riskLevel = :level AND c.deletedAt IS NULL")
    Optional<RiskLevelConfig> findActiveByRiskLevel(@Param("level") RiskLevel level);
}
