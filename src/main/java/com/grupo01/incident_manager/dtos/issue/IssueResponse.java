package com.grupo01.incident_manager.dtos.issue;

import java.time.LocalDateTime;

public record IssueResponse(
        Long id,
        String ticketCode,
        String title,
        String description,
        String type,
        String status,
        String priority,
        Long idProject,
        String projectKey,
        String creatorName,
        String assigneeName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
