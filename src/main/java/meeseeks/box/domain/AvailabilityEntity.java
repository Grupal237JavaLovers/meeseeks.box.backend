package meeseeks.box.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */
@Entity
@Table(name = "Availability", uniqueConstraints = @UniqueConstraint(columnNames = {"day", "start_hour", "end_hour"}))
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
    @OrderBy("id")
    @ManyToMany(mappedBy = "availabilities")
    private List<JobEntity> jobs = new ArrayList<>();

    private static final Time DEFAULT_HOUR = new Time(0);
    private static final String DEFAULT_DAY = "";

    public AvailabilityEntity() {
        this(DEFAULT_DAY, DEFAULT_HOUR, DEFAULT_HOUR);
    }

    public AvailabilityEntity(final String day,
                              final Time startHour,
                              final Time endHour) {
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(final String day) {
        this.day = day;
    }

    public Time getStartHour() {
        return startHour;
    }

    public void setStartHour(final Time startHour) {
        this.startHour = startHour;
    }

    public Time getEndHour() {
        return endHour;
    }

    public void setEndHour(final Time endHour) {
        this.endHour = endHour;
    }

    public List<JobEntity> getJobs() {
        return jobs;
    }

    public void setJobs(final List<JobEntity> jobs) {
        this.jobs = jobs;
    }
}
