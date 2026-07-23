package com.grupo01.incident_manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grupo01.incident_manager.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByKey(String key);

    Optional<Project> findByInviteCode(String inviteCode);

    boolean existsByKey(String key);

    @Query("""
                SELECT DISTINCT p FROM Project p
                LEFT JOIN MemberProject mp ON mp.project.id = p.id
                WHERE p.author.id = :userId OR mp.user.id = :userId
            """)
    List<Project> findProjectsByUserId(@Param("userId") Long userId);
}
