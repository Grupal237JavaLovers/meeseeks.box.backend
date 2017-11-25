package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ConsumerEntity extends UserEntity implements Serializable {

    private static final String DEFAULT = "";

    @Column(name = "profile_image")
    private String profileImageUrl;

    @JsonIgnore
    @OrderBy("id")
    @OneToMany(mappedBy = "consumer", targetEntity = JobEntity.class, cascade = CascadeType.REMOVE)
    private List<JobEntity> jobs = new ArrayList<>();

    @JsonIgnore
    @OrderBy("id")
    @OneToMany(mappedBy = "consumer", targetEntity = ReviewEntity.class, cascade = CascadeType.REMOVE)
    private List<RequestEntity> reviews = new ArrayList<>();

    public ConsumerEntity(final String username,
                          final String password,
                          final String name,
                          final String email) {
        this(username, password, name, email, DEFAULT);
    }

    public ConsumerEntity() {
        this(DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT);
    }

    public ConsumerEntity(final String username,
                          final String password,
                          final String name,
                          final String email,
                          final String profileImageUrl) {
        super(email, username, password, name);
        this.profileImageUrl = profileImageUrl;
        this.setRole(UserRole.consumer);
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(final String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<JobEntity> getJobs() {
        return jobs;
    }

    public void setJobs(final List<JobEntity> jobs) {
        this.jobs = jobs;
    }

    public List<RequestEntity> getReviews() {
        return reviews;
    }

    public void setReviews(final List<RequestEntity> reviews) {
        this.reviews = reviews;
    }
}
