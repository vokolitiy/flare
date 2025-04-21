package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "epic_id")
    private Epic epic;

    private int storyPoints;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "story_priority_id", referencedColumnName = "id")
    private StoryPriority storyPriority;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "story_progress_id", referencedColumnName = "id")
    private StoryProgress storyProgress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "story_creator_id", referencedColumnName = "id")
    private User storyCreator;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "story_resolution_id", referencedColumnName = "id")
    private StoryResolution storyResolution;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "story_assignee_id", referencedColumnName = "id")
    private User storyAssignee;

    @OneToMany(mappedBy = "storyWatchers", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<User> storyWatchers;

    @OneToMany(mappedBy = "storyTasks", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Task> storyTasks;
}
