package com.grupo01.incident_manager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo01.incident_manager.dtos.project.ProjectRequest;
import com.grupo01.incident_manager.dtos.project.ProjectResponse;
import com.grupo01.incident_manager.exception.ResourceAlreadyExistsException;
import com.grupo01.incident_manager.exception.ResourceNotFoundException;
import com.grupo01.incident_manager.model.IssueScheme;
import com.grupo01.incident_manager.model.Project;
import com.grupo01.incident_manager.model.User;
import com.grupo01.incident_manager.repository.HistoryRepository;
import com.grupo01.incident_manager.repository.IssueRepository;
import com.grupo01.incident_manager.repository.IssueSchemeRepository;
import com.grupo01.incident_manager.repository.MemberProjectRepository;
import com.grupo01.incident_manager.repository.ProjectRepository;
import com.grupo01.incident_manager.repository.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final IssueSchemeRepository issueSchemeRepository;
    private final MemberProjectRepository memberProjectRepository;
    private final IssueRepository issueRepository;
    private final HistoryRepository historyRepository;

    @SuppressWarnings("null")
    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {

        String formattedKey = request.key().trim().toUpperCase();

        // Verificamos que no exista otro projecto con la misma KEY
        if (projectRepository.existsByKey(formattedKey)) {
            throw new ResourceAlreadyExistsException("Ya existe un proyecto con la KEY: " + formattedKey);
        }

        // Buscamos al usuario en la BD
        User creator = userRepository.findById(request.idUser())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario creador no encontrado"));

        // Buscamos el esquema de incidencias en la BD
        IssueScheme scheme = issueSchemeRepository.findById(request.idIssueScheme())
                .orElseThrow(() -> new ResourceNotFoundException("Esquema de incidencias no encontrado"));

        // Contruimos el proyecto
        Project project = Project.builder()
                .name(request.name().trim())
                .key(formattedKey)
                .description(request.description())
                .author(creator)
                .scheme(scheme)
                .build();

        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);

    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        String currentEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        return projectRepository.findProjectsByUserId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(@NonNull Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        return mapToResponse(project);
    }

    @Transactional
    public void deleteProject(@NonNull Long id) {
        // Verificamos si el proyecto existe
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("El proyecto no existe");
        }

        historyRepository.deleteByIssue_Project_Id(id);
        memberProjectRepository.deleteByProject_Id(id);
        issueRepository.deleteByProject_Id(id);

        projectRepository.deleteById(id);
    }

    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getKey(),
                project.getDescription(),
                project.getScheme().getId(),
                project.getScheme().getName(),
                project.getAuthor().getId(),
                project.getAuthor().getName(),
                project.getInviteCode(),
                project.getCreatedAt());
    }
}
