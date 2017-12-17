package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Entity
@Table(name = "Job")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @Column(name = "name")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;

    @Column(name = "description")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String description;

    @Column(name = "location")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String location;

    @Column(name = "type")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String type;

    @Column(name = "price")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Double price;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "expiration_date")
    private Calendar expiration;

    @ManyToOne(fetch = FetchType.EAGER,
            targetEntity = CategoryEntity.class,
            cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category")
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.EAGER,
            targetEntity = ConsumerEntity.class)
    @JoinColumn(name = "id_consumer")
    private ConsumerEntity consumer;

    @JsonIgnore
    @OrderBy("id")
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "AvailabilityList", joinColumns = @JoinColumn(name = "id_job", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_availability", referencedColumnName = "id"))
    private List<AvailabilityEntity> availabilities = new ArrayList<>();

    @JsonIgnore
    @OrderBy("id")
    @OneToMany(mappedBy = "job", targetEntity = RequestEntity.class)
    private List<RequestEntity> requests = new ArrayList<>();

    @JsonIgnore
    @OrderBy("id")
    @OneToMany(mappedBy = "job", targetEntity = ReviewEntity.class)
    private List<RequestEntity> reviews = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @CreationTimestamp
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Calendar created;

    private static final String DEFAULT = "";

    public JobEntity(final String name,
                     final String description,
                     final String location,
                     final String type,
                     final Double price) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.type = type;
        this.price = price;
        this.created= null;
    }

    public JobEntity() {
        this(DEFAULT, DEFAULT, DEFAULT, DEFAULT, 1.0);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public Calendar getExpiration() {
        return expiration;
    }

    public void setExpiration(final Calendar expiration) {
        this.expiration = expiration;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(final CategoryEntity category) {
        this.category = category;
    }

    public List<AvailabilityEntity> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(final List<AvailabilityEntity> availabilities) {
        this.availabilities = availabilities;
    }

    public ConsumerEntity getConsumer() {
        return consumer;
    }

    public void setConsumer(final ConsumerEntity consumer) {
        this.consumer = consumer;
    }

    public List<RequestEntity> getRequests() {
        return requests;
    }

    public void setRequests(final List<RequestEntity> requests) {
        this.requests = requests;
    }

    public List<RequestEntity> getReviews() {
        return reviews;
    }

    public void setReviews(final List<RequestEntity> reviews) {
        this.reviews = reviews;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }
}
