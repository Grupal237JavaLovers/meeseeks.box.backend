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

@SuppressWarnings("all")
@Entity
@Table(name = "Skill")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkillEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @Column(name = "name", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "skills")
    private Set<ProviderEntity> providers = new HashSet<>();

    public SkillEntity(String name) {
        this.name = name;
    }

    public SkillEntity() {
        this("");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ProviderEntity> getProviders() {
        return providers;
    }

    public void setProviders(Set<ProviderEntity> providers) {
        this.providers = providers;
    }
}
