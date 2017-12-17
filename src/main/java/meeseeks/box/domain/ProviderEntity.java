package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @OneToMany(mappedBy = "provider", targetEntity = ReviewEntity.class, cascade = CascadeType.REMOVE)
    private List<RequestEntity> reviews = new ArrayList<>();

    @JsonIgnore
    @OrderBy("id")
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "SkillList", joinColumns = @JoinColumn(name = "id_provider", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_skill", referencedColumnName = "id"))
    private List<SkillEntity> skills = new ArrayList<>();

    @JsonIgnore
    @OrderBy("date")
    @OneToMany(mappedBy = "provider", targetEntity = RequestEntity.class, cascade = CascadeType.ALL)
    private List<RequestEntity> requests = new ArrayList<>();

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

    public ProviderEntity(final String username, final String password) {
        this(username, password, "default", "default@test.com");
    }

    public ProviderEntity(final String username, final String password, final String email) {
        this(username, password, DEFAULT, email);
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(final String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getProfileVideoUrl() {
        return profileVideoUrl;
    }

    public void setProfileVideoUrl(final String profileVideoUrl) {
        this.profileVideoUrl = profileVideoUrl;
    }

    public List<SkillEntity> getSkills() {
        return skills;
    }

    public void setSkills(final List<SkillEntity> skills) {
        this.skills = skills;
    }

    public List<RequestEntity> getReviews() {
        return reviews;
    }

    public void setReviews(final List<RequestEntity> reviews) {
        this.reviews = reviews;
    }

    public List<RequestEntity> getRequests() {
        return requests;
    }

    public void setRequests(final List<RequestEntity> requests) {
        this.requests = requests;
    }

}
