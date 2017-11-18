package meeseeks.box.model;

import meeseeks.box.domain.AvailabilityEntity;
import meeseeks.box.domain.CategoryEntity;
import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

public class JobModel implements Serializable {

    private final JobEntity job;
    private final Set<AvailabilityEntity> availabilities;
    private final CategoryEntity category;
    private final ConsumerEntity consumer;

    public JobModel() {
        this(null, null, null, null);
    }
    public JobModel(final JobEntity job,
                    final Set<AvailabilityEntity> availabilities,
                    final CategoryEntity category,
                    final ConsumerEntity consumer) {
        this.job = job;
        this.availabilities = availabilities;
        this.category = category;
        this.consumer = consumer;
    }

    public JobEntity getJob() {
        return job;
    }

    public Set<AvailabilityEntity> getAvailabilities() {
        return availabilities;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public ConsumerEntity getConsumer() {
        return consumer;
    }

    public JobEntity build() {
        job.setCategory(category);
        job.setAvailabilities(availabilities);
        job.setConsumer(consumer);
        return job;
    }
}
