package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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


@SuppressWarnings({"WeakerAccess", "unused"})
@Entity
@Table(name = "Request")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @CreationTimestamp
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Calendar date;

    @Column(name = "message")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @Column(name = "accepted")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Boolean accepted;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ProviderEntity.class)
    @JoinColumn(name = "id_provider")
    private ProviderEntity provider;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = JobEntity.class)
    @JoinColumn(name = "id_job")
    private JobEntity job;

    private static final String DEFAULT = "";
    private static final Boolean DEFAULT_STATUS = Boolean.FALSE;

    public RequestEntity() {
        this(DEFAULT, DEFAULT_STATUS);
    }

    public RequestEntity(final @NotNull String message, final @NotNull Boolean isAccepted) {
        this.message = message;
        this.accepted = isAccepted;
    }

    public RequestEntity(final ProviderEntity provider, final JobEntity job, final String message) {
        this.provider = provider;
        this.job = job;
        this.message = message;
        this.accepted = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final @NotNull Integer id) {
        this.id = id;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(final @NotNull Calendar date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final @NotNull String message) {
        this.message = message;
    }

    public Boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(final @NotNull Boolean accepted) {
        this.accepted = accepted;
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

