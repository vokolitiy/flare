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

    @OneToOne
    private Task taskProgress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setTaskProgress(Task taskProgress) {
        this.taskProgress = taskProgress;
    }
}
