package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.core.annotation.Order;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @OrderBy("id")
    @ManyToMany(mappedBy = "skills")
    private List<ProviderEntity> providers = new ArrayList<>();

    private static final String DEFAULT = "";

    public SkillEntity(final String name) {
        this(0, name);
    }

    public SkillEntity() {
        this(DEFAULT);
    }

    public SkillEntity(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<ProviderEntity> getProviders() {
        return providers;
    }

    public void setProviders(final List<ProviderEntity> providers) {
        this.providers = providers;
    }
}
