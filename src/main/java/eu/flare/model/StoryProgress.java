package eu.flare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "story_progresses")
public class StoryProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 15)
    private String name;

    @OneToOne(mappedBy = "storyProgress")
    private Story story;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
