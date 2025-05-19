package eu.flare.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false, length = 20)
    private String username;
    @Column(unique = true, nullable = false, length = 20)
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
            )
    @JsonManagedReference
    private List<Role> roles = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_story_watchers",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Story> watchedStories;

    @OneToMany(mappedBy = "storyAssignee")
    @JsonManagedReference
    private List<Story> assignedStories;

    @OneToMany(mappedBy = "storyAssignee")
    @JsonManagedReference
    private List<Story> createdStories;

    @OneToOne
    private Task taskCreator;

    @OneToOne
    private Task taskAssignee;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JsonIgnore
    @JsonProperty(value = "password")
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @JsonIgnore
    @JsonProperty(value = "email")
    public String getEmail() {
        return email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public long getId() {
        return id;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @JsonIgnore
    @JsonProperty("taskCreator")
    public Task getTaskCreator() {
        return taskCreator;
    }

    public void setTaskCreator(Task taskCreator) {
        this.taskCreator = taskCreator;
    }

    @JsonIgnore
    @JsonProperty("taskAssignee")
    public Task getTaskAssignee() {
        return taskAssignee;
    }

    public void setTaskAssignee(Task taskAssignee) {
        this.taskAssignee = taskAssignee;
    }

    public List<Story> getAssignedStories() {
        return assignedStories;
    }

    public void setAssignedStories(List<Story> assignedStories) {
        this.assignedStories = assignedStories;
    }

    public List<Story> getCreatedStories() {
        return createdStories;
    }

    public void setCreatedStories(List<Story> createdStories) {
        this.createdStories = createdStories;
    }

    public List<Story> getWatchedStories() {
        return watchedStories;
    }

    public void setWatchedStories(List<Story> watchedStories) {
        this.watchedStories = watchedStories;
    }
}
