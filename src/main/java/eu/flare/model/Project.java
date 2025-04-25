package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 30)
    private String name;
    @CreationTimestamp
    private Date startedAt;
    @UpdateTimestamp
    private Date updatedAt;
    private Date expectedEndDate;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<User> projectMembers;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sprint_id")
    private List<Sprint> sprints;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Epic> epics;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "backlog_id", referencedColumnName = "id")
    private Backlog backlog;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Epic> getEpics() {
        return epics;
    }

    public void setEpics(List<Epic> epics) {
        this.epics = epics;
    }

    public List<User> getProjectMembers() {
        return projectMembers;
    }

    public void setProjectMembers(List<User> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public long getId() {
        return id;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }
}
