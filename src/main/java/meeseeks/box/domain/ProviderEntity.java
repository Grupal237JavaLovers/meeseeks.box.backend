package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
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
@SuppressWarnings("all")
public class ProviderEntity extends UserEntity implements Serializable {

    private static final String DEFAULT = "";

    @Column(name = "profile_image")
    private String profileImageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "video_url")
    private String profileVideoUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "provider", targetEntity = ReviewEntity.class)
    private Set<RequestEntity> reviews = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "SkillList", joinColumns = @JoinColumn(name = "id_provider", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_skill", referencedColumnName = "id"))
    private Set<SkillEntity> skills = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "provider", targetEntity = RequestEntity.class)
    private Set<RequestEntity> requests = new HashSet<>();

    public ProviderEntity(final String username,
                          final String password,
                          final String name,
                          final String email) {
        this(username, password, name, email, DEFAULT, DEFAULT, DEFAULT);
    }

    public ProviderEntity() {
        this(DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT);
    }

    @SuppressWarnings("WeakerAccess")
    public ProviderEntity(final String username,
                          final String password,
                          final String name,
                          final String email,
                          final String profileImageUrl,
                          final String description,
                          final String profileVideoUrl) {
        super(email, username, password, name);
        this.profileImageUrl = profileImageUrl;
        this.description = description;
        this.profileVideoUrl = profileVideoUrl;
        this.setRole(UserRole.provider);
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(final @NotNull String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final @NotNull String description) {
        this.description = description;
    }

    public String getProfileVideoUrl() {
        return profileVideoUrl;
    }

    public void setProfileVideoUrl(final @NotNull String profileVideoUrl) {
        this.profileVideoUrl = profileVideoUrl;
    }

    public Set<SkillEntity> getSkills() {
        return skills;
    }

    public void setSkills(final @NotNull Set<SkillEntity> skills) {
        this.skills = skills;
    }

    public Set<RequestEntity> getReviews() {
        return reviews;
    }

    public void setReviews(final @NotNull Set<RequestEntity> reviews) {
        this.reviews = reviews;
    }

    public Set<RequestEntity> getRequests() {
        return requests;
    }

    public void setRequests(final @NotNull Set<RequestEntity> requests) {
        this.requests = requests;
    }

}
