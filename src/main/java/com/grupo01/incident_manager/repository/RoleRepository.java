package com.grupo01.incident_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo01.incident_manager.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
