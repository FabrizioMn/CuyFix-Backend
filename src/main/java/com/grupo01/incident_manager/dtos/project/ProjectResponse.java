package com.grupo01.incident_manager.dtos.project;

import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String name,
        String key,
        String description,
        Long idIssueScheme,
        String issueSchemeName,
        Long idUser,
        String userName,
        String inviteCode,
        LocalDateTime createdAt) {

}
