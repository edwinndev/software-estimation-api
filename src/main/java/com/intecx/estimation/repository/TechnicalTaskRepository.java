package com.intecx.estimation.repository;

import com.intecx.estimation.entity.TechnicalTask;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TechnicalTaskRepository extends JpaRepository<TechnicalTask, UUID>, JpaSpecificationExecutor<TechnicalTask> {

    List<TechnicalTask> findAllByStoryIdOrderByPositionAsc(UUID storyId);
}
