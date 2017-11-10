package meeseeks.box.model;

import meeseeks.box.domain.AvailabilityEntity;
import meeseeks.box.domain.CategoryEntity;
import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;

import java.io.Serializable;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

public class JobModel implements Serializable {

    private final JobEntity job;
    private final AvailabilityEntity availability;
    private final CategoryEntity category;
    private final ConsumerEntity consumer;

    public JobModel() {
        this(null, null, null, null);
    }
    public JobModel(final JobEntity job,
                    final AvailabilityEntity availability,
                    final CategoryEntity category,
                    final ConsumerEntity consumer) {
        this.job = job;
        this.availability = availability;
        this.category = category;
        this.consumer = consumer;
    }

    public JobEntity getJob() {
        return job;
    }

    public AvailabilityEntity getAvailability() {
        return availability;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public ConsumerEntity getConsumer() {
        return consumer;
    }

    public JobEntity build() {
        job.setCategory(category);
        job.setAvailability(availability);
        job.setConsumer(consumer);
        return job;
    }
}
