package com.intecx.estimation.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_assignments")
@Getter
@Setter
@NoArgsConstructor
public class TaskAssignment {

    @EmbeddedId
    private TaskProfileId id;
}