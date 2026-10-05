package com.intecx.inscope.repository;

import com.intecx.inscope.entity.PasswordResetToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    @Query("SELECT t FROM PasswordResetToken t WHERE t.email = :email AND t.consumedAt IS NULL ORDER BY t.createdAt DESC LIMIT 1")
    Optional<PasswordResetToken> findLatestActiveByEmail(@Param("email") String email);

    @Query("SELECT t FROM PasswordResetToken t WHERE t.email = :email AND t.verifiedAt IS NOT NULL AND t.consumedAt IS NULL ORDER BY t.createdAt DESC LIMIT 1")
    Optional<PasswordResetToken> findLatestVerifiedByEmail(@Param("email") String email);
}
