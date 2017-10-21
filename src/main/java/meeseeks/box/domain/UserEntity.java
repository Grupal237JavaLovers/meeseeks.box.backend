package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Entity
@Table(name="User")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEntity implements UserDetails, Serializable {

    @Id
    @GeneratedValue
    @Column
    private Integer id;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String firstName;

    @Column
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String lastName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @CreationTimestamp
    private Calendar created;

    public UserEntity() {
        this(null, null, null, null);
    }

    public UserEntity(final String email,
                      final String password,
                      final String firstName,
                      final String lastName) {
        super();
        this.id = null;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Calendar getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName;
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