package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @ManyToOne
    @JoinColumn(name = "story_id")
    @JsonBackReference
    private Story storyTasks;

    @OneToOne(mappedBy = "taskPriority")
    @JsonManagedReference
    private TaskPriority taskPriority;

    @OneToOne(mappedBy = "taskProgress")
    @JsonManagedReference
    private TaskProgress taskProgress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "task_resolution_id")
    private TaskResolution taskResolution;

    @OneToOne(mappedBy = "taskCreator")
    @JsonManagedReference
    private User taskCreator;

    @OneToOne(mappedBy = "taskAssignee")
    @JsonManagedReference
    private User taskAssignee;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEstimatedCompletionDate() {
        return estimatedCompletionDate;
    }

    public void setEstimatedCompletionDate(Date estimatedCompletionDate) {
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    public long getOriginalEstimate() {
        return originalEstimate;
    }

    public void setOriginalEstimate(long originalEstimate) {
        this.originalEstimate = originalEstimate;
    }

    public long getRemainingEstimate() {
        return remainingEstimate;
    }

    public void setRemainingEstimate(long remainingEstimate) {
        this.remainingEstimate = remainingEstimate;
    }

    public Story getStoryTasks() {
        return storyTasks;
    }

    public void setStoryTasks(Story storyTasks) {
        this.storyTasks = storyTasks;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public TaskProgress getTaskProgress() {
        return taskProgress;
    }

    public void setTaskProgress(TaskProgress taskProgress) {
        this.taskProgress = taskProgress;
    }

    public User getTaskCreator() {
        return taskCreator;
    }

    public void setTaskCreator(User taskCreator) {
        this.taskCreator = taskCreator;
    }

    public User getTaskAssignee() {
        return taskAssignee;
    }

    public void setTaskAssignee(User taskAssignee) {
        this.taskAssignee = taskAssignee;
    }

    public long getId() {
        return id;
    }
}
