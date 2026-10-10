package com.intecx.inscope.repository;

import com.intecx.inscope.entity.TaskAssignment;
import com.intecx.inscope.entity.TaskProfileId;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, TaskProfileId> {

    List<TaskAssignment> findByIdTaskIdIn(Collection<UUID> taskIds);
}