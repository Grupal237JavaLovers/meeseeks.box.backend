package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@SuppressWarnings("unused")
@Entity
@Table(name = "User")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEntity implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @Email(message = "{provider.email.incorrect}")
    @Column(name = "email", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String email;

    @Size(min = 8, message = "{provider.password.length}")
    @Column(name = "password")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String password;

    @Column(name = "name")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;

    @Column(name = "username", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String username;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @CreationTimestamp
    private Calendar created;

    private static final String DEFAULT = "";

    public UserEntity() {
        this(DEFAULT, DEFAULT, DEFAULT, DEFAULT);
    }

    public UserEntity(final @NotNull String email,
                      final @NotNull String username,
                      final @NotNull String password,
                      final @NotNull String name) {
        super();
        this.id = null;
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final @NotNull Integer id) {
        this.id = id;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(final @NotNull String email) {
        this.email = email;
    }

    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(final @NotNull String password) {
        this.password = password;
    }

    public @NotNull Calendar getCreated() {
        return created;
    }

    public void setName(final @NotNull String name) {
        this.name = name;
    }

    public void setUsername(final @NotNull String username) {
        this.username = username;
    }

    public void setCreated(final @NotNull Calendar created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}