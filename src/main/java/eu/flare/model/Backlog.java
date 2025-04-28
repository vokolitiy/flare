package eu.flare.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "backlog")
public class Backlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 150)
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "story_id", referencedColumnName = "id")
    private List<Story> backlogStories;

    public List<Story> getBacklogStories() {
        return backlogStories;
    }

    public void setBacklogStories(List<Story> backlogStories) {
        this.backlogStories = backlogStories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }
}
