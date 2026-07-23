package com.grupo01.incident_manager.dtos.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "El nombre del proyecto es obligatorio") 
        String name,

        @NotBlank(message = "La clave del proyecto es obligatoria") 
        @Size(min = 2, max = 5, message = "La clave del proyecto debe tener entre 2 y 5 caracteres") 
        String key,
        
        String description,

        @NotNull(message = "El esquema de incidencias es obligatorio")
        Long idIssueScheme,

        @NotNull(message = "El usuario responsable del proyecto es obligatorio")
        Long idUser
) {

}
