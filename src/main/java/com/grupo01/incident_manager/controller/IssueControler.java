package com.grupo01.incident_manager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo01.incident_manager.dtos.issue.IssueRequest;
import com.grupo01.incident_manager.dtos.issue.IssueResponse;
import com.grupo01.incident_manager.service.IssueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueControler {

    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody IssueRequest request) {
        return ResponseEntity.ok(issueService.createIssue(request));
    }

    @GetMapping("/project/{idProject}")
    public ResponseEntity<List<IssueResponse>> getIssuesByProject(@PathVariable Long idProject) {
        return ResponseEntity.ok(issueService.getIssueByProject(idProject));
    }

    @PatchMapping("/{idIssue}/status")
    public ResponseEntity<IssueResponse> updateIssueStatus(
            @PathVariable Long idIssue,
            @RequestParam String status) {
        return ResponseEntity.ok(issueService.updateIssueStatus(idIssue, status));
    }

}
