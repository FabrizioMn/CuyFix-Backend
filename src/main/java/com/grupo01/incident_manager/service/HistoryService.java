package com.grupo01.incident_manager.service;

import com.grupo01.incident_manager.repository.HistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo01.incident_manager.dtos.history.HistoryResponse;
import com.grupo01.incident_manager.model.History;
import com.grupo01.incident_manager.model.Issue;
import com.grupo01.incident_manager.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    @SuppressWarnings("null")
    @Transactional
    public void createLog(Issue issue, User user, String field, String oldValue, String newValue) {
        // Verificamos si el valor no cambio
        if (oldValue != null && oldValue.equals(newValue)) {
            return;
        }

        History history = History.builder()
                .issue(issue)
                .user(user)
                .changedField(field)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedAt(LocalDateTime.now())
                .build();
        History saveHistory = historyRepository.save(history);
        mapToResponse(saveHistory);

    }

    @Transactional(readOnly = true)
    public List<HistoryResponse> getHistoryByIssue(Long idIssue) {
        return historyRepository.findByIssue_IdOrderByChangedAtDesc(idIssue).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private HistoryResponse mapToResponse(History history) {
        return new HistoryResponse(
                history.getId(),
                history.getIssue().getId(),
                history.getIssue().getTicketCode(),
                history.getUser().getName(),
                history.getChangedField(),
                history.getOldValue(),
                history.getNewValue(),
                history.getChangedAt());
    }

}
