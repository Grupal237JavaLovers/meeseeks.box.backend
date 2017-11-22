package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */
@Entity
@Table(name = "Review")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReviewEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @Column(name = "message")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @CreationTimestamp
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Calendar date;

    @Column(name = "rating")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer rating;

    @Column(name = "received_provider")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Boolean receivedByProvider;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ConsumerEntity.class)
    @JoinColumn(name = "id_consumer")
    private ConsumerEntity consumer;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = ProviderEntity.class)
    @JoinColumn(name = "id_provider")
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = JobEntity.class)
    @JoinColumn(name = "id_job")
    private JobEntity job;

    public ReviewEntity() {
        this("", -1);
    }

    public ReviewEntity(final @NotNull String message, final @NotNull Integer rating) {
        this.message = message;
        this.rating = rating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final @NotNull Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final @NotNull String message) {
        this.message = message;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(final @NotNull Calendar date) {
        this.date = date;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(final @NotNull Integer rating) {
        this.rating = rating;
    }

    public Boolean isReceivedByProvider() {
        return receivedByProvider;
    }

    public void setReceivedByProvider(final @NotNull Boolean receivedByProvider) {
        this.receivedByProvider = receivedByProvider;
    }

    public ConsumerEntity getConsumer() {
        return consumer;
    }

    public void setConsumer(final @NotNull ConsumerEntity consumer) {
        this.consumer = consumer;
    }

    public ProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(final @NotNull ProviderEntity provider) {
        this.provider = provider;
    }

    public JobEntity getJob() {
        return job;
    }

    public void setJob(final @NotNull JobEntity job) {
        this.job = job;
    }
}
