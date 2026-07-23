package com.grupo01.incident_manager.dtos.issue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IssueRequest(

    @NotBlank(message = "El título de la incidencia es obligatorio")
    String title,
    
    String description,
    
    @NotBlank(message = "El tipo de incidencia es obligatorio")
    String type,
    
    @NotBlank(message = "La prioridad (HIGH, MEDIUM, LOW) es obligatoria")
    String priority,
    
    @NotNull(message = "El ID del proyecto es obligatorio")
    Long idProject,
    
    @NotNull(message = "El ID del creador de la incidencia es obligatorio")
    Long idCreator,
    
    Long idAssignee
) {
    
}
