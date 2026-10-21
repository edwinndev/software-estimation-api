package com.intecx.inscope.repository;

import com.intecx.inscope.entity.TechnicalProfile;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicalProfileRepository extends JpaRepository<TechnicalProfile, UUID>, JpaSpecificationExecutor<TechnicalProfile> {

    Optional<TechnicalProfile> findByIdAndDeletedAtIsNull(UUID id);

    List<TechnicalProfile> findByIdInAndDeletedAtIsNullAndIsActiveTrue(Collection<UUID> ids);

    boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);

    boolean existsByEmailIgnoreCaseAndDeletedAtIsNullAndIdNot(String email, UUID id);
}
