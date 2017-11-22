package meeseeks.box.domain;

import java.io.Serializable;
import java.sql.Time;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */
@Entity
@Table(name = "Availability", uniqueConstraints=@UniqueConstraint(columnNames={"day", "start_hour", "end_hour"}))
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailabilityEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @Column(name = "day")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String day;

    @Column(name = "start_hour")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Time startHour;

    @Column(name = "end_hour")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Time endHour;

    @JsonIgnore
    @ManyToMany(mappedBy = "availabilities")
    private Set<JobEntity> jobs = new HashSet<>();

    private static final Time DEFAULT_HOUR = new Time(0);
    private static final String DEFAULT_DAY = "";

    public AvailabilityEntity() {
        this(DEFAULT_DAY, DEFAULT_HOUR, DEFAULT_HOUR);
    }

    public AvailabilityEntity(final @NotNull String day,
                              final @NotNull Time startHour,
                              final @NotNull Time endHour) {
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final @NotNull Integer id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(final @NotNull String day) {
        this.day = day;
    }

    public Time getStartHour() {
        return startHour;
    }

    public void setStartHour(final @NotNull Time startHour) {
        this.startHour = startHour;
    }

    public Time getEndHour() {
        return endHour;
    }

    public void setEndHour(final @NotNull Time endHour) {
        this.endHour = endHour;
    }

    public Set<JobEntity> getJobs() {
        return jobs;
    }

    public void setJobs(final @NotNull Set<JobEntity> jobs) {
        this.jobs = jobs;
    }
}
