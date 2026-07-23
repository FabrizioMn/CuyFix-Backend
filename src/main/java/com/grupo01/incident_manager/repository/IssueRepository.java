package com.grupo01.incident_manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grupo01.incident_manager.model.Issue;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByProject_IdAndStatus(Long idProject, String status);

    List<Issue> findByProject_Id(Long idProject);

    List<Issue> findByAssignee_IdAndStatusNot(Long idUser, String status);

    Optional<Issue> findByTicketCode(String ticketCode);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.project.id=:idProject")
    long countByProject_Id(@Param("idProject") Long idProject);

    void deleteByProject_Id(Long idProject);
}
