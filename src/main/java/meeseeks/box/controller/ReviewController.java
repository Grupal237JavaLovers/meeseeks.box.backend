package meeseeks.box.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @PostMapping("/insert/{job}/provider/{id}")
    public ReviewEntity insertForProvider(@PathVariable("job") final Integer job, @PathVariable("id") final Integer id,
            @RequestBody @Valid ReviewEntity review) {
        JobEntity jobEntity = jobRepository.findById(job).orElseThrow(() -> new NotFoundException("Job not found"));
        ProviderEntity provider = getProviderById(id);

        ConsumerEntity consumer = (ConsumerEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Get the managed consumer from the database because Hibernate weirdness
        consumer = consumerRepository.findOne(consumer.getId());

        review.setProvider(provider);
        review.setConsumer(consumer);
        review.setReceivedByProvider(true);
        review.setJob(jobEntity);

        return reviewRepository.save(review);
    }

    @Secured({"ROLE_PROVIDER"})
    @ResponseBody
    @PostMapping("/insert/{job}/consumer/{id}")
    public ReviewEntity insertForConsumer(@PathVariable("job") final Integer job, @PathVariable("id") final Integer id,
            @RequestBody @Valid ReviewEntity review) {
        JobEntity jobEntity = jobRepository.findById(job).orElseThrow(() -> new NotFoundException("Job not found"));
        ConsumerEntity consumer = getConsumerById(id);

        ProviderEntity provider = (ProviderEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Get the managed provider from the database because Hibernate weirdness
        provider = providerRepository.findOne(provider.getId());

        review.setProvider(provider);
        review.setConsumer(consumer);
        review.setReceivedByProvider(false);
        review.setJob(jobEntity);

        return reviewRepository.save(review);
    }

    @ResponseBody
    @GetMapping("/latest/provider/{id}/{limit}/{received}")
    public List<ReviewEntity> getLatestReviewsProvider(@PathVariable("id") final Integer id,
            @PathVariable("limit") final Integer limit, @PathVariable("received") final Boolean received) throws NotFoundException {
        return reviewRepository.findLatestReviewsProvider(getProviderById(id), received, new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/latest/consumer/{id}/{limit}/{received}")
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
