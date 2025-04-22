package eu.flare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "task_progresses")
public class TaskProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 15)
    private String name;

}
