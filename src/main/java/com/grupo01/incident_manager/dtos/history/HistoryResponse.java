package com.grupo01.incident_manager.dtos.history;

import java.time.LocalDateTime;

public record HistoryResponse(
        Long id,
        Long idIssue,
        String tickerCode,
        String userName,
        String changedField,
        String oldValue,
        String newValue,
        LocalDateTime changedAt) {

}
