package com.grupo01.incident_manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo01.incident_manager.model.History;

public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByIssue_IdOrderByChangedAtDesc(Long idIssue);

    void deleteByIssue_Project_Id(Long idProject);
}
