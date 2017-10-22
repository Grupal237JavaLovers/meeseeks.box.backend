package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

@SuppressWarnings("unused")
@Entity
@Table(name = "Category")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CategoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @Column(name = "name", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category", targetEntity = JobEntity.class)
    private Set<JobEntity> jobs = new HashSet<>();

    private static final String DEFAULT = "";

    public CategoryEntity(final @NotNull String name) {
        this.name = name;
    }

    public CategoryEntity() {
        this(DEFAULT);
    }

    public @NotNull Integer getId() {
        return id;
    }

    public void setId(final @NotNull Integer id) {
        this.id = id;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(final @NotNull String name) {
        this.name = name;
    }

    public @NotNull Set<JobEntity> getJobs() {
        return jobs;
    }

    public void setJobs(final @NotNull Set<JobEntity> jobs) {
        this.jobs = jobs;
    }
}
