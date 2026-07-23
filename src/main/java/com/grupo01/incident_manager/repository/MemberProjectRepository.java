package com.grupo01.incident_manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo01.incident_manager.model.MemberProject;
import com.grupo01.incident_manager.model.MemberProjectId;

public interface MemberProjectRepository extends JpaRepository<MemberProject, MemberProjectId> {

    List<MemberProject> findByUser_Id(Long idUser);

    List<MemberProject> findByProject_Id(Long idProject);

    boolean existsByProject_IdAndUser_Id(Long idProject, Long idUser);

    void deleteByProject_Id(Long idProject);

}
