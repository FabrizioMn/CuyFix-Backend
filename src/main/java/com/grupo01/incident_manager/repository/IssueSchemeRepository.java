package com.grupo01.incident_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo01.incident_manager.model.IssueScheme;

public interface IssueSchemeRepository extends JpaRepository<IssueScheme, Long> {

}
