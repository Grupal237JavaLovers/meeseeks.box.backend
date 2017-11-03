package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
    @OneToMany(mappedBy = "consumer", targetEntity = JobEntity.class)
    private Set<JobEntity> jobs = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "consumer", targetEntity = ReviewEntity.class)
    private Set<ReviewEntity> reviews = new HashSet<>();

    public ConsumerEntity(final @NotNull String username,
                          final @NotNull String password,
                          final @NotNull String name,
                          final @NotNull String email) {
        this(username, password, name, email, DEFAULT);
    }

    public ConsumerEntity() {
        this(DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT);
    }

    public ConsumerEntity(final @NotNull String username,
                          final @NotNull String password,
                          final @NotNull String name,
                          final @NotNull String email,
                          final @NotNull String profileImageUrl) {
        super(email, username, password, name);
        this.profileImageUrl = profileImageUrl;
    }

    public @NotNull String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(final @NotNull String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public @NotNull Set<JobEntity> getJobs() {
        return jobs;
    }

    public void setJobs(final @NotNull Set<JobEntity> jobs) {
        this.jobs = jobs;
    }

    public @NotNull Set<ReviewEntity> getReviews() {
        return reviews;
    }

    public void setReviews(final @NotNull Set<ReviewEntity> reviews) {
        this.reviews = reviews;
    }
}
