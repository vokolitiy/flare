package eu.flare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "story_priorities")
public class StoryPriority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 15)
    private String name;

    @OneToOne(mappedBy = "storyPriority")
    private Story story;
}
