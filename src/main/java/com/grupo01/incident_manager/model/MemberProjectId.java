package com.grupo01.incident_manager.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberProjectId implements Serializable {

    @Column(name = "id_project")
    private Long idProject;

    @Column(name = "id_user")
    private Long idUser;
}
