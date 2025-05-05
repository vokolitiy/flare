package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sprints")
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 30)
    private String name;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToMany(mappedBy = "sprintStory")
    @JsonManagedReference
    private List<Story> sprintStories;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sprint_board_id", referencedColumnName = "id")
    private Board sprintBoard;

    private Date completeDate;

    private boolean isCompleted;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Board getSprintBoard() {
        return sprintBoard;
    }

    public void setSprintBoard(Board sprintBoard) {
        this.sprintBoard = sprintBoard;
    }

    public List<Story> getSprintStories() {
        return sprintStories;
    }

    public void setSprintStories(List<Story> sprintStories) {
        this.sprintStories = sprintStories;
    }
}
