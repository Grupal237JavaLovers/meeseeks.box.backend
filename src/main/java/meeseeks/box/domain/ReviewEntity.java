package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy hh:mm")
    private Calendar date;

    @Column(name = "rating")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer rating;

    @Column(name = "received_provider")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Boolean receivedByProvider;

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

    public ReviewEntity(final String message, final Integer rating) {
        this(message, rating, null, null, false);
    }

    public ReviewEntity(
            final String message,
            final Integer rating,
            final ConsumerEntity consumer,
            final ProviderEntity provider,
            final Boolean receivedByProvider) {
        this.message = message;
        this.rating = rating;
        this.consumer = consumer;
        this.provider = provider;
        this.receivedByProvider = receivedByProvider;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(final Calendar date) {
        this.date = date;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }

    public Boolean getReceivedByProvider() {
        return receivedByProvider;
    }

    public void setReceivedByProvider(final Boolean receivedByProvider) {
        this.receivedByProvider = receivedByProvider;
    }

    public ConsumerEntity getConsumer() {
        return consumer;
    }

    public void setConsumer(final ConsumerEntity consumer) {
        this.consumer = consumer;
    }

    public ProviderEntity getProvider() {
        return provider;
    }

    public void setProvider(final ProviderEntity provider) {
        this.provider = provider;
    }

    public JobEntity getJob() {
        return job;
    }

    public void setJob(final JobEntity job) {
        this.job = job;
    }
}
