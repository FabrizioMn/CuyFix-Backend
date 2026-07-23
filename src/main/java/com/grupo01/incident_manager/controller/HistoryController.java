package com.grupo01.incident_manager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo01.incident_manager.dtos.history.HistoryResponse;
import com.grupo01.incident_manager.service.HistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/issue/{idIssue}")
    public ResponseEntity<List<HistoryResponse>> getHistoryByIssue(@PathVariable Long idIssue) {
        return ResponseEntity.ok(historyService.getHistoryByIssue(idIssue));
    }
}
