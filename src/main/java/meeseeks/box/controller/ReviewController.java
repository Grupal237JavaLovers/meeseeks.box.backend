package meeseeks.box.controller;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.ReviewEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public ReviewEntity insertForProvider(@PathVariable("job") final Integer job,
                                          @PathVariable("id") final Integer id,
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
    public ReviewEntity insertForConsumer(@PathVariable("job") final Integer job,
                                          @PathVariable("id") final Integer id,
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
    @Secured("ROLE_PROVIDER")
    @PostMapping("/update/consumer/{id}")
    public ResponseEntity<ReviewEntity> updateRatingReviewForConsumer(@PathVariable("id") final Integer id,
                                                                      @RequestParam(required = false, value = "rating") final Integer rating,
                                                                      @RequestParam(required = false, value = "message") final String message) {
        ReviewEntity review = getReviewById(id);
        Integer finalRating = rating != null ? rating : review.getRating();
        String finalMessage = message != null ? message : review.getMessage();
        return reviewRepository.updateReviewForConsumer(id, finalRating, finalMessage) > 0 ?
                new ResponseEntity<>(getReviewById(id), HttpStatus.ACCEPTED) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @Secured("ROLE_PROVIDER")
    @PostMapping("/update/provider/{id}")
    public ResponseEntity<ReviewEntity> updateRatingReviewForProvider(@PathVariable("id") final Integer id,
                                                                      @RequestParam(required = false, value = "rating") final Integer rating,
                                                                      @RequestParam(required = false, value = "message") final String message) {
        ReviewEntity review = getReviewById(id);
        Integer finalRating = rating != null ? rating : review.getRating();
        String finalMessage = message != null ? message : review.getMessage();
        return reviewRepository.updateReviewForProvider(id, finalRating, finalMessage) > 0 ?
                new ResponseEntity<>(getReviewById(id), HttpStatus.ACCEPTED) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @GetMapping("/latest/provider/{id}/{limit}/{received}")
    public List<ReviewEntity> getLatestReviewsProvider(@PathVariable("id") final Integer id,
                                                       @PathVariable("limit") final Integer limit,
                                                       @PathVariable("received") final Boolean received) throws NotFoundException {
        return reviewRepository.findLatestReviewsProvider(getProviderById(id), received, new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/latest/consumer/{id}/{limit}/{received}")
    public List<ReviewEntity> getLatestReviewsConsumer(@PathVariable("id") final Integer id,
                                                       @PathVariable("limit") final Integer limit,
                                                       @PathVariable("received") final Boolean received) throws NotFoundException {
        return reviewRepository.findLatestReviewsConsumer(getConsumerById(id), received, new PageRequest(0, limit));
    }

    private ReviewEntity getReviewById(final Integer id) {
        return reviewRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Review Not Found!"));
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
