package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Entity
@Table(name = "User")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEntity implements UserDetails, Serializable {

    private static final String DEFAULT = "";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(access = Access.READ_ONLY)
    private Integer id;

    @Email(message = "{provider.email.incorrect}", groups={ValidationRegister.class, ValidationEdit.class})
    @Column(name = "email", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String email;

    @Size(min = 8, message = "{provider.password.length}", groups={ValidationRegister.class})
    @Column(name = "password")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    @Column(name = "name")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;

    @Column(name = "username", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String username;

    @Enumerated(EnumType.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = Access.READ_ONLY)
    private UserRole role;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @CreationTimestamp
    @JsonProperty(access = Access.READ_ONLY)
    private Calendar created;

    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private String confirmPassword;

    public UserEntity() {
        this(DEFAULT, DEFAULT, DEFAULT, DEFAULT);
    }

    public UserEntity(final @NotNull String email,
                      final @NotNull String username,
                      final String password,
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

    public void setName(final @NotNull String name) {
        this.name = name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Calendar getCreated() {
        return created;
    }

    public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	@Override
    public String toString() {
        return this.name;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	List<GrantedAuthority> auths = new ArrayList<>();

    	if (this.getRole() != null) {
    	    auths.add(new SimpleGrantedAuthority(this.getRole().toString()));
    	}

        // All users should also have the default user role
        if (this.getRole() != UserRole.user) {
            auths.add(new SimpleGrantedAuthority(UserRole.user.toString()));
        }

        return auths;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(final @NotNull String username) {
        this.username = username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @JsonIgnore
    @AssertTrue(message = "{provider.passwords.mismatch}", groups={ValidationRegister.class})
    public boolean isPasswordConfirmed() {
        return getPassword() != null && this.confirmPassword != null
                && getPassword().equals(this.confirmPassword);
    }

    public static enum UserRole {

    	user("ROLE_USER"),
        provider("ROLE_PROVIDER"),
        consumer("ROLE_CONSUMER");

        private String name;

        UserRole(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public interface ValidationRegister {}
    public interface ValidationEdit {}
}