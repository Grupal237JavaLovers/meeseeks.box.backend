package meeseeks.box.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.ReviewEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.ReviewRepository;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProviderRepository providerRepository;
    private final ConsumerRepository consumerRepository;
    private final JobRepository jobRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ReviewController(final ReviewRepository reviewRepository,
            final ProviderRepository providerRepository,
            final ConsumerRepository consumerRepository,
            final JobRepository jobRepository) {
        this.reviewRepository = reviewRepository;
        this.providerRepository = providerRepository;
        this.consumerRepository = consumerRepository;
        this.jobRepository = jobRepository;
    }

    @Secured({"ROLE_CONSUMER"})
    @ResponseBody
    @RequestMapping(value = "/insert/{job}/provider/{id}", method={RequestMethod.POST})
    @Transactional
    public ReviewEntity insertForProvider(@PathVariable("job") final Integer job, @PathVariable("id") final Integer id,
            @RequestBody @Valid ReviewEntity review, @AuthenticationPrincipal ConsumerEntity consumer) {
        JobEntity jobEntity = jobRepository.findById(job).orElseThrow(() -> new NotFoundException("Job not found"));
        ProviderEntity provider = getProviderById(id);

        // Re-attach the consumer to be managed by Hibernate. Weird Hibernate stuff, just leave it as it is.
        consumer = entityManager.merge(consumer);

        review.setProvider(provider);
        review.setConsumer(consumer);
        review.setReceivedByProvider(true);
        review.setJob(jobEntity);

        return reviewRepository.save(review);
    }

    @Secured({"ROLE_PROVIDER"})
    @ResponseBody
    @RequestMapping(value = "/insert/{job}/consumer/{id}", method={RequestMethod.POST})
    @Transactional
    public ReviewEntity insertForConsumer(@PathVariable("job") final Integer job, @PathVariable("id") final Integer id,
            @RequestBody @Valid ReviewEntity review, @AuthenticationPrincipal ProviderEntity provider) {
        JobEntity jobEntity = jobRepository.findById(job).orElseThrow(() -> new NotFoundException("Job not found"));
        ConsumerEntity consumer = getConsumerById(id);

        // Re-attach the consumer to be managed by Hibernate. Weird Hibernate stuff, just leave it as it is.
        provider = entityManager.merge(provider);

        review.setProvider(provider);
        review.setConsumer(consumer);
        review.setReceivedByProvider(false);
        review.setJob(jobEntity);

        return reviewRepository.save(review);
    }

    @ResponseBody
    @RequestMapping("/latest/provider/{id}/{limit}/{received}")
    public List<ReviewEntity> getLatestReviewsProvider(@PathVariable("id") final Integer id,
            @PathVariable("limit") final Integer limit, @PathVariable("received") final Boolean received) throws NotFoundException {
        return reviewRepository.findLatestReviewsProvider(getProviderById(id), received, new PageRequest(0, limit));
    }

    @ResponseBody
    @RequestMapping("/latest/consumer/{id}/{limit}/{received}")
    public List<ReviewEntity> getLatestReviewsConsumer(@PathVariable("id") final Integer id,
            @PathVariable("limit") final Integer limit, @PathVariable("received") final Boolean received) throws NotFoundException {
        return reviewRepository.findLatestReviewsConsumer(getConsumerById(id), received, new PageRequest(0, limit));
    }

    private ProviderEntity getProviderById(final Integer id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider not found"));
    }

    private ConsumerEntity getConsumerById(final Integer id) {
        return consumerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consumer not found"));
    }
}
