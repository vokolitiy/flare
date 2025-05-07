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

    @Enumerated(value = EnumType.STRING)
    private PriorityType priorityType;

    @Enumerated(value = EnumType.STRING)
    private ProgressType progressType;

    @Enumerated(value = EnumType.STRING)
    private ResolutionType resolutionType;

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

    public PriorityType getPriorityType() {
        return priorityType;
    }

    public void setPriorityType(PriorityType priorityType) {
        this.priorityType = priorityType;
    }

    public ProgressType getProgressType() {
        return progressType;
    }

    public void setProgressType(ProgressType progressType) {
        this.progressType = progressType;
    }

    public ResolutionType getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(ResolutionType resolutionType) {
        this.resolutionType = resolutionType;
    }
}
