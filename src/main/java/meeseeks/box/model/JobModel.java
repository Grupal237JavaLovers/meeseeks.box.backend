package meeseeks.box.model;

import meeseeks.box.domain.AvailabilityEntity;
import meeseeks.box.domain.CategoryEntity;
import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

public class JobModel implements Serializable {

    @NotNull
    private final JobEntity job;
    @NotNull
    private Set<AvailabilityEntity> availabilities;
    @NotNull
    private CategoryEntity category;

    public JobModel() {
        this(null, null, null);
    }
    public JobModel(final JobEntity job,
                    final Set<AvailabilityEntity> availabilities,
                    final CategoryEntity category) {
        this.job = job;
        this.availabilities = availabilities;
        this.category = category;
    }

    public JobEntity getJob() {
        return job;
    }

    public void setAvailabilities(Set<AvailabilityEntity> availabilities) {
        this.availabilities = availabilities;
    }

    public Set<AvailabilityEntity> getAvailabilities() {
        return availabilities;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public JobEntity build(ConsumerEntity consumer) {
        job.setCategory(category);
        job.setAvailabilities(availabilities);
        job.setConsumer(consumer);
        return job;
    }
}
