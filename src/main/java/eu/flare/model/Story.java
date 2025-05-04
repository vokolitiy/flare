package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "stories")
public class Story {
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
    @JoinColumn(name = "epic_id")
    @JsonBackReference
    private Epic epic;

    @ManyToOne
    @JoinColumn(name = "backlog_id")
    @JsonBackReference
    private Backlog backlog;

    private int storyPoints;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "storyPriority", orphanRemoval = true)
    @JsonManagedReference
    private StoryPriority storyPriority;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "storyProgress", orphanRemoval = true)
    @JsonManagedReference
    private StoryProgress storyProgress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "story_resolution_id", referencedColumnName = "id")
    private StoryResolution storyResolution;

    @ManyToMany(mappedBy = "watchedStories", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<User> storyWatchers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "story_creator_id")
    @JsonBackReference
    private User storyCreator;

    @ManyToOne
    @JoinColumn(name = "story_assignee_id")
    @JsonBackReference
    private User storyAssignee;

    @OneToMany(mappedBy = "storyTasks")
    @JsonManagedReference
    private List<Task> storyTasks;

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

    public long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
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

    public Epic getEpic() {
        return epic;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
    }

    public StoryPriority getStoryPriority() {
        return storyPriority;
    }

    public void setStoryPriority(StoryPriority storyPriority) {
        this.storyPriority = storyPriority;
    }

    public StoryProgress getStoryProgress() {
        return storyProgress;
    }

    public void setStoryProgress(StoryProgress storyProgress) {
        this.storyProgress = storyProgress;
    }

    public User getStoryCreator() {
        return storyCreator;
    }

    public void setStoryCreator(User storyCreator) {
        this.storyCreator = storyCreator;
    }

    public StoryResolution getStoryResolution() {
        return storyResolution;
    }

    public void setStoryResolution(StoryResolution storyResolution) {
        this.storyResolution = storyResolution;
    }

    public User getStoryAssignee() {
        return storyAssignee;
    }

    public void setStoryAssignee(User storyAssignee) {
        this.storyAssignee = storyAssignee;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public List<Task> getStoryTasks() {
        return storyTasks;
    }

    public void setStoryTasks(List<Task> storyTasks) {
        this.storyTasks = storyTasks;
    }

    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    public List<User> getStoryWatchers() {
        return storyWatchers;
    }

    public void setStoryWatchers(List<User> storyWatchers) {
        this.storyWatchers = storyWatchers;
    }
}
