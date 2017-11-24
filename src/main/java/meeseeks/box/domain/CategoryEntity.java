package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @OrderBy("id")
    @OneToMany(mappedBy = "category", targetEntity = JobEntity.class)
    private List<JobEntity> jobs = new ArrayList<>();

    private static final String DEFAULT = "";

    public CategoryEntity(final String name) {
        this.name = name;
    }

    public CategoryEntity() {
        this(DEFAULT);
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

    public List<JobEntity> getJobs() {
        return jobs;
    }

    public void setJobs(final List<JobEntity> jobs) {
        this.jobs = jobs;
    }
}
