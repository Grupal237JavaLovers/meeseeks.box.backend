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
@Table(name = "Category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @Column(name = "name", unique = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;

    public CategoryEntity(final String name) {
        this.name = name;
    }

    public CategoryEntity() {
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
}
