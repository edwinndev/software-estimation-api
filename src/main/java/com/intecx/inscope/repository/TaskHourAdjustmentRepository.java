package com.intecx.inscope.repository;

import com.intecx.inscope.entity.TaskHourAdjustment;
import com.intecx.inscope.entity.TaskProfileId;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskHourAdjustmentRepository extends JpaRepository<TaskHourAdjustment, TaskProfileId> {

    List<TaskHourAdjustment> findByIdTaskIdIn(Collection<UUID> taskIds);
}