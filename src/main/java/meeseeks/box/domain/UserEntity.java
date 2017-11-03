package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private Integer id;
    @Email(message = "{provider.email.incorrect}")
    @Column(name = "email", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String email;
    @Size(min = 8, message = "{provider.password.length}")
    @Column(name = "password")
//    @JsonIgnore
    private String password;
    @Column(name = "name")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;
    @Column(name = "username", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String username;
    @Enumerated(EnumType.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserRole role;
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @CreationTimestamp
    private Calendar created;
    @Transient
    private String confirmPassword;

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

    public void setName(final @NotNull String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(final @NotNull String password) {
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @AssertTrue(message = "{provider.passwords.mismatch}")
    public boolean isPasswordConfirmed() {
        return getPassword() != null && this.confirmPassword != null
                && getPassword().equals(this.confirmPassword);
    }

    public static enum UserRole {
    	user("ROLE_USER"),
        provider("ROLE_PROVIDER"),
        consumer("ROLE_CONSUMER");

        private String nume;

        UserRole(String nume) {
            this.nume = nume;
        }

        @Override
        public String toString() {
            return this.nume;
        }
    }

}