package com.grupo01.incident_manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grupo01.incident_manager.model.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByToken(String token);

    @Query("""
                SELECT t FROM UserToken t
                WHERE t.user.id=:idUser
                AND (t.expired=false OR t.revoked=false)
            """)
    List<UserToken> findAllValidTokensByUser(@Param("idUser") Long idUser);
}
