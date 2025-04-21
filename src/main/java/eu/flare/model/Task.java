package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 150)
    private String name;
    private String description;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    private Date estimatedCompletionDate;
    private long originalEstimate;
    private long remainingEstimate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "story_id")
    private Story storyTasks;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_priority_id")
    private TaskPriority taskPriority;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_progress_id")
    private TaskProgress taskProgress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_resolution_id")
    private TaskResolution taskResolution;
}
