package com.jubhub.respository;

import com.jubhub.model.entity.RefreshToken;
import com.jubhub.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revokedAt = CURRENT_TIMESTAMP WHERE r.user = :user AND r.revokedAt IS NULL")
    void revokeAllByUser(@Param("user") User user);

    void deleteByTokenHash(String tokenHash);

    void deleteByUser(User user);

    void deleteByExpiresAtBefore(Instant now);
}
