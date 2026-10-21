package com.intecx.inscope.repository;

import com.intecx.inscope.entity.ReportSnapshot;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportSnapshotRepository extends JpaRepository<ReportSnapshot, UUID> {

    @Query("""
            SELECT snapshot
            FROM ReportSnapshot snapshot
            JOIN FETCH snapshot.project
            WHERE snapshot.project.id IN :projectIds
            ORDER BY snapshot.createdAt DESC, snapshot.id
            """)
    List<ReportSnapshot> findByProjectIds(@Param("projectIds") Collection<UUID> projectIds);

    Optional<ReportSnapshot> findByIdAndProject_IdAndProject_DeletedAtIsNull(UUID id, UUID projectId);
}
