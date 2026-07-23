package com.grupo01.incident_manager.dtos.auth;

public record LoginRequest(
        String email,
        String password) {
}
