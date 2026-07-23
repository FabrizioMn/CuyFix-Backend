package com.grupo01.incident_manager.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "member_project")
@Builder
public class MemberProject {

    @EmbeddedId
    private MemberProjectId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProject")
    @JoinColumn(name = "id_project")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUser")
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "project_rol", nullable = false, length = 50)
    private String projectRol;

    @Column(name = "join_date", nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @PrePersist
    protected void onCreate() {
        this.joinDate = LocalDateTime.now();
        if (this.id == null) {
            this.id = new MemberProjectId();
        }
        if (project != null) {
            this.id.setIdProject(project.getId());
        }
        if (user != null) {
            this.id.setIdUser(user.getId());
        }
    }
}
