package com.intecx.inscope.repository;

import com.intecx.inscope.entity.TechnicalProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicalProfileRepository extends JpaRepository<TechnicalProfile, UUID>, JpaSpecificationExecutor<TechnicalProfile> {

    @Query("SELECT p FROM TechnicalProfile p WHERE LOWER(p.email) = LOWER(:email) AND p.deletedAt IS NULL")
    Optional<TechnicalProfile> findActiveByEmail(@Param("email") String email);

    @Query("SELECT p FROM TechnicalProfile p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<TechnicalProfile> findActiveById(@Param("id") UUID id);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM TechnicalProfile p WHERE LOWER(p.email) = LOWER(:email) AND p.deletedAt IS NULL")
    boolean existsActiveByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM TechnicalProfile p WHERE LOWER(p.email) = LOWER(:email) AND p.deletedAt IS NULL AND p.id <> :id")
    boolean existsActiveByEmailAndIdNot(@Param("email") String email, @Param("id") UUID id);
}
