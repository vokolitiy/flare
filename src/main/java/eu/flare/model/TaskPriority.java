package eu.flare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "task_priorities")
public class TaskPriority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 15)
    private String name;

    @OneToOne(mappedBy = "taskPriority")
    private Task taskPriority;
}
