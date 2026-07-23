package com.grupo01.incident_manager.dtos.auth;

public record RegisterRequest(
    String email,
    String password,
    String name,
    Long idRol
) {
}
