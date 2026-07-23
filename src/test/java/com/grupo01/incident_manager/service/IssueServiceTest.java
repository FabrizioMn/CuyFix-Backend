package com.grupo01.incident_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.grupo01.incident_manager.dtos.issue.IssueRequest;
import com.grupo01.incident_manager.dtos.issue.IssueResponse;
import com.grupo01.incident_manager.exception.BadRequestException;
import com.grupo01.incident_manager.exception.ResourceNotFoundException;
import com.grupo01.incident_manager.model.Issue;
import com.grupo01.incident_manager.model.Project;
import com.grupo01.incident_manager.model.User;
import com.grupo01.incident_manager.repository.IssueRepository;
import com.grupo01.incident_manager.repository.MemberProjectRepository;
import com.grupo01.incident_manager.repository.ProjectRepository;
import com.grupo01.incident_manager.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {
    @Mock
    private IssueRepository issueRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MemberProjectRepository memberProjectRepository;

    @InjectMocks
    private IssueService issueService;

    private Project mockProject;
    private User mockCreator;
    private User mockAssignee;

    @BeforeEach
    void setUp() {
        mockCreator = User.builder().id(1L).name("Admin").email("admin@test.com").build();
        mockAssignee = User.builder().id(2L).name("Dev").email("dev@test.com").build();

        mockProject = Project.builder()
                .id(10L)
                .name("Project Test")
                .key("TEST")
                .author(mockCreator)
                .build();
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Debe crear una incidencia correctamente cuando los datos son válidos")
    void createIssue_Success() {
        IssueRequest request = new IssueRequest("Error al iniciar sesión", "Bug en login", "BUG", "HIGH", 10L, 1L, 2L);

        // arrange
        when(projectRepository.findById(10L)).thenReturn(Optional.of(mockProject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockCreator));
        when(memberProjectRepository.existsByProject_IdAndUser_Id(10L, 2L)).thenReturn(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockAssignee));
        when(issueRepository.countByProject_Id(10L)).thenReturn(0L);

        when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> {
            Issue issueSaved = invocation.getArgument(0);
            issueSaved.setId(100L);
            return issueSaved;
        });

        // Act
        IssueResponse response = issueService.createIssue(request);

        // Assert
        assertNotNull(response);
        assertEquals("TEST-1", response.ticketCode());
        assertEquals("Error al iniciar sesión", response.title());
        assertEquals("BACKLOG", response.status());
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    @DisplayName("Debe listar las incidencias pertenecientes a un proyecto existente")
    void getIssueByProject_Success() {
        // Arrange
        Issue issue = Issue.builder()
                .id(100L)
                .ticketCode("TEST-1")
                .title("Ajuste CSS")
                .type("TASK")
                .status("BACKLOG")
                .priority("LOW")
                .project(mockProject)
                .creator(mockCreator)
                .build();

        when(projectRepository.existsById(10L)).thenReturn(true);
        when(issueRepository.findByProject_Id(10L)).thenReturn(List.of(issue));

        // Act
        List<IssueResponse> list = issueService.getIssueByProject(10L);

        // Assert
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals("TEST-1", list.get(0).ticketCode());
    }

    @Test
    @DisplayName("Debe lanzar BadRequestException cuando el usuario a asignar no es miembro del proyecto")
    void createIssue_ThrowsException_WhenAssigneeIsNotMember() {
        IssueRequest request = new IssueRequest("Tarea", "Desc", "TASK", "MEDIUM", 10L, 1L, 99L); // 99L no es miembro

        when(projectRepository.findById(10L)).thenReturn(Optional.of(mockProject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockCreator));
        when(memberProjectRepository.existsByProject_IdAndUser_Id(10L, 99L)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> issueService.createIssue(request));
        verify(issueRepository, never()).save(any(Issue.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el proyecto buscado no existe")
    void getIssueByProject_ThrowsException_WhenProjectNotFound() {
        // Arrange
        when(projectRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> issueService.getIssueByProject(99L));
    }

}
