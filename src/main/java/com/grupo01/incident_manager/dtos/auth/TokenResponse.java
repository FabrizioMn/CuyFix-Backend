package com.grupo01.incident_manager.dtos.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("access_token") 
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken) {
}
