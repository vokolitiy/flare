package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "story_progresses")
public class StoryProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 15)
    private String name;

    @OneToOne
    @JsonBackReference
    private Story storyProgress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public Story getStoryProgress() {
        return storyProgress;
    }

    public void setStoryProgress(Story storyProgress) {
        this.storyProgress = storyProgress;
    }
}
