package com.grupo01.incident_manager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo01.incident_manager.dtos.memberProject.MemberProjectRequest;
import com.grupo01.incident_manager.dtos.memberProject.MemberProjectResponse;
import com.grupo01.incident_manager.exception.ResourceAlreadyExistsException;
import com.grupo01.incident_manager.exception.ResourceNotFoundException;
import com.grupo01.incident_manager.model.MemberProject;
import com.grupo01.incident_manager.model.MemberProjectId;
import com.grupo01.incident_manager.model.Project;
import com.grupo01.incident_manager.model.User;
import com.grupo01.incident_manager.repository.MemberProjectRepository;
import com.grupo01.incident_manager.repository.ProjectRepository;
import com.grupo01.incident_manager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberProjectService {

    private final MemberProjectRepository memberProjectRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @SuppressWarnings("null")
    @Transactional
    public MemberProjectResponse joinProjectByCode(String inviteCode, Long idUser) {
        Project project = projectRepository.findByInviteCode(inviteCode.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Código de invitación no válido."));

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        if (memberProjectRepository.existsByProject_IdAndUser_Id(project.getId(), user.getId())) {
            throw new ResourceAlreadyExistsException("Ya eres miembro de este proyecto.");
        }

        MemberProjectId idCompound = new MemberProjectId(project.getId(), user.getId());
        MemberProject memberProject = MemberProject.builder()
                .id(idCompound)
                .project(project)
                .user(user)
                .projectRol("DEVELOPER")
                .build();

        MemberProject savedMember = memberProjectRepository.save(memberProject);
        return mapToResponse(savedMember);
    }

    @SuppressWarnings("null")
    @Transactional
    public MemberProjectResponse addMemberToProject(MemberProjectRequest request) {

        if (memberProjectRepository.existsByProject_IdAndUser_Id(request.idProject(), request.idUser())) {
            throw new ResourceAlreadyExistsException("El usuario ya es miembro de este proyecto");
        }

        Project project = projectRepository.findById(request.idProject())
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado."));

        User user = userRepository.findById(request.idUser())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        MemberProjectId idCompound = new MemberProjectId(request.idProject(), request.idUser());

        MemberProject memberProject = MemberProject.builder()
                .id(idCompound)
                .project(project)
                .user(user)
                .projectRol(request.projectRole().trim().toUpperCase())
                .build();
        MemberProject savedMember = memberProjectRepository.save(memberProject);
        return mapToResponse(savedMember);

    }

    @Transactional(readOnly = true)
    public List<MemberProjectResponse> getMembersByProject(Long idProject) {
        return memberProjectRepository.findByProject_Id(idProject).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeMemberFromProject(Long idProject, Long idUser) {
        MemberProjectId idCompound = new MemberProjectId(idProject, idUser);
        if (!memberProjectRepository.existsById(idCompound)) {
            throw new ResourceNotFoundException("El usuario no pertenece a este proyecto");
        }

        memberProjectRepository.deleteById(idCompound);
    }

    private MemberProjectResponse mapToResponse(MemberProject member) {
        return new MemberProjectResponse(
                member.getProject().getId(),
                member.getProject().getName(),
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getProjectRol(),
                member.getJoinDate());
    }
}
