package com.intecx.inscope.repository;

import com.intecx.inscope.entity.SprintConfig;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintConfigRepository extends JpaRepository<SprintConfig, UUID> {
}