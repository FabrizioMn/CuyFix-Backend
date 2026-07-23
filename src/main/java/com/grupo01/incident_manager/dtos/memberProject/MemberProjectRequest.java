package com.grupo01.incident_manager.dtos.memberProject;

import jakarta.validation.constraints.NotNull;

public record MemberProjectRequest(

    @NotNull(message = "El ID del proyecto es obligatorio")
    Long idProject,
    
    @NotNull(message = "El ID del usuario es obligatorio")
    Long idUser,
    
    @NotNull(message = "El rol del proyecto es obligatorio")
    String projectRole
) {
    
}
