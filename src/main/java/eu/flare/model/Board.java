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
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 30)
    private String name;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToOne
    @JoinColumn(name = "sprint_id", referencedColumnName = "id")
    @JsonBackReference
    private Sprint sprint;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "boards_todo_stories",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Story> todoStories = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "boards_progress_stories",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Story> inProgressStories = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "boards_inreview_stories",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Story> reviewStories = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "boards_done_stories",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Story> doneStories = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public List<Story> getTodoStories() {
        return todoStories;
    }

    public void setTodoStories(List<Story> todoStories) {
        this.todoStories = todoStories;
    }

    public List<Story> getInProgressStories() {
        return inProgressStories;
    }

    public void setInProgressStories(List<Story> inProgressStories) {
        this.inProgressStories = inProgressStories;
    }

    public List<Story> getReviewStories() {
        return reviewStories;
    }

    public void setReviewStories(List<Story> reviewStories) {
        this.reviewStories = reviewStories;
    }

    public List<Story> getDoneStories() {
        return doneStories;
    }

    public void setDoneStories(List<Story> doneStories) {
        this.doneStories = doneStories;
    }
}
