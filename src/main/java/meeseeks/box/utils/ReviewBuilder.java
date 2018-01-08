package meeseeks.box.utils;

import meeseeks.box.domain.*;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

public class ReviewBuilder {

    private ReviewEntity review;
    private ProviderEntity provider;
    private ConsumerEntity consumer;
    private JobEntity job;
    private Boolean recieved;

    public ReviewBuilder(final ReviewEntity review) {
        this.review = review;
    }

    public ReviewBuilder setProvider(final ProviderEntity provider) {
        review.setProvider(provider);
        return this;
    }

    public ReviewBuilder setConsumer(final ConsumerEntity consumer) {
        review.setConsumer(consumer);
        return this;
    }
    public ReviewBuilder setJob(final JobEntity job) {
        review.setJob(job);
        return this;
    }

    public ReviewBuilder setRecievedByProvider(final Boolean recieved) {
        review.setReceivedByProvider(recieved);
        return this;
    }

    public ReviewEntity build() {
        return review;
    }
}
