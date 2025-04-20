package eu.flare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
