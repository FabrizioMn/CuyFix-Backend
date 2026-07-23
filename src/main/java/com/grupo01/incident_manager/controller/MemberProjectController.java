package com.grupo01.incident_manager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo01.incident_manager.dtos.memberProject.MemberProjectRequest;
import com.grupo01.incident_manager.dtos.memberProject.MemberProjectResponse;
import com.grupo01.incident_manager.service.MemberProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/project-members")
@RequiredArgsConstructor
public class MemberProjectController {
    private final MemberProjectService memberProjectService;

    @PostMapping("/join")
    public ResponseEntity<MemberProjectResponse> joinProject(@RequestParam String inviteCode,
            @RequestParam Long idUser) {
        return ResponseEntity.ok(memberProjectService.joinProjectByCode(inviteCode, idUser));
    }

    @PostMapping
    public ResponseEntity<MemberProjectResponse> addMemberToProject(@Valid @RequestBody MemberProjectRequest request) {
        return ResponseEntity.ok(memberProjectService.addMemberToProject(request));
    }

    @GetMapping("/project/{idProject}")
    public ResponseEntity<List<MemberProjectResponse>> getMembersByProject(@PathVariable Long idProject) {
        return ResponseEntity.ok(memberProjectService.getMembersByProject(idProject));
    }

    @DeleteMapping("/project/{idProject}/user/{idUser}")
    public ResponseEntity<Void> removeMemberFromProject(
            @PathVariable Long idProject,
            @PathVariable Long idUser) {
        memberProjectService.removeMemberFromProject(idProject, idUser);
        return ResponseEntity.noContent().build();
    }

}
