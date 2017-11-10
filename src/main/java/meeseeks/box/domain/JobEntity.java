package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne(fetch = FetchType.LAZY,
            targetEntity = CategoryEntity.class,
            cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category")
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY,
            targetEntity = AvailabilityEntity.class,
            cascade = CascadeType.PERSIST)
    @JoinColumn(name = "availability")
    private AvailabilityEntity availability;

    @ManyToOne(fetch = FetchType.LAZY,
            targetEntity = ConsumerEntity.class,
            cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_consumer")
    private ConsumerEntity consumer;

    @JsonIgnore
    @OneToMany(mappedBy = "job", targetEntity = RequestEntity.class)
    private Set<RequestEntity> requests = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "job", targetEntity = ReviewEntity.class)
    private Set<RequestEntity> reviews = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @CreationTimestamp
    private Calendar created;

    private static final String DEFAULT = "";

    public JobEntity(final @NotNull String name,
                     final @NotNull String description,
                     final @NotNull String location,
                     final @NotNull String type,
                     final @NotNull Double price) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.type = type;
        this.price = price;
    }

    public JobEntity() {
        this(DEFAULT, DEFAULT, DEFAULT, DEFAULT, 1.0);
    }

    public Integer getId() {
        return id;
    }

    public void setId(final @NotNull Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final @NotNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final @NotNull String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final @NotNull String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(final @NotNull String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(final @NotNull Double price) {
        this.price = price;
    }

    public Calendar getExpiration() {
        return expiration;
    }

    public void setExpiration(final @NotNull Calendar expiration) {
        this.expiration = expiration;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(final @NotNull CategoryEntity category) {
        this.category = category;
    }

    public AvailabilityEntity getAvailability() {
        return availability;
    }

    public void setAvailability(final @NotNull AvailabilityEntity availability) {
        this.availability = availability;
    }

    public ConsumerEntity getConsumer() {
        return consumer;
    }

    public void setConsumer(final @NotNull ConsumerEntity consumer) {
        this.consumer = consumer;
    }

    public Set<RequestEntity> getRequests() {
        return requests;
    }

    public void setRequests(final @NotNull Set<RequestEntity> requests) {
        this.requests = requests;
    }

    public Set<RequestEntity> getReviews() {
        return reviews;
    }

    public void setReviews(final @NotNull Set<RequestEntity> reviews) {
        this.reviews = reviews;
    }

    public Calendar getCreated() {return created;}
}
