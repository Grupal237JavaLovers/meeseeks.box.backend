package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Entity
@Table(name = "Provider")
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("all")
public class ProviderEntity implements Serializable {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @Column(name = "username", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String username;

    @Column(name = "password")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String password;

    @Column(name = "name")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;

    @Column(name = "email", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String email;

    @Column(name = "profile_image")
    private String profileImageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "video_url")
    private String profileVideoUrl;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "SkillList", joinColumns = @JoinColumn(name = "id_provider", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_skill", referencedColumnName = "id"))
    private Set<SkillEntity> skills = new HashSet<>();

    private static final String DEFAULT = "";

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
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.description = description;
        this.profileVideoUrl = profileVideoUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileVideoUrl() {
        return profileVideoUrl;
    }

    public void setProfileVideoUrl(String profileVideoUrl) {
        this.profileVideoUrl = profileVideoUrl;
    }

    public Set<SkillEntity> getSkills() {
        return skills;
    }

    public void setSkills(Set<SkillEntity> skills) {
        this.skills = skills;
    }
}
