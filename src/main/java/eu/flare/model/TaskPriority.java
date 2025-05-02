package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "task_priorities")
public class TaskPriority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 15)
    private String name;

    @OneToOne
    @JsonBackReference
    private Task taskPriority;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Task getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(Task taskPriority) {
        this.taskPriority = taskPriority;
    }
}
