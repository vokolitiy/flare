package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 150)
    private String name;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToOne(mappedBy = "sprintBoard")
    @JsonBackReference
    private Sprint sprintBoard;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "board_todo_stories",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Story> todoStories;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "board_progress_stories",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Story> inProgressStories;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "board_review_stories",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Story> inReviewStories;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "board_done_stories",
            joinColumns = @JoinColumn(name = "board_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Story> doneStories;

    public long getId() {
        return id;
    }

    public String geName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Sprint getSprintBoard() {
        return sprintBoard;
    }

    public void setSprintBoard(Sprint sprintBoard) {
        this.sprintBoard = sprintBoard;
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

    public List<Story> getInReviewStories() {
        return inReviewStories;
    }

    public void setInReviewStories(List<Story> inReviewStories) {
        this.inReviewStories = inReviewStories;
    }

    public List<Story> getDoneStories() {
        return doneStories;
    }

    public void setDoneStories(List<Story> doneStories) {
        this.doneStories = doneStories;
    }
}
