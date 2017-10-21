package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@SuppressWarnings("all")
@Entity
@Table(name = "Consumer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumerEntity implements Serializable {

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

    private static final String DEFAULT = "";

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
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
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

}
