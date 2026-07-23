package com.grupo01.incident_manager.dtos.memberProject;

import java.time.LocalDateTime;

public record MemberProjectResponse(
                Long idProject,
                String projectName,
                Long idUser,
                String userName,
                String userEmail,
                String projectRole,
                LocalDateTime joinDate) {

}
