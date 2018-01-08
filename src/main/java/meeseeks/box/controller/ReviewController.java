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
import meeseeks.box.utils.ReviewBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @FunctionalInterface
    private interface TriFunction<T, U, X, R> {
        R apply(T param1, U param2, X param3);
    }

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

    @ResponseBody
    @Secured({"ROLE_CONSUMER"})
    @PostMapping("/insert/{job}/provider/{id}")
    public ReviewEntity insertReviewForProvider(@PathVariable("job") final Integer idJob,
                                                @PathVariable("id") final Integer idProvider,
                                                @RequestBody @Valid ReviewEntity review,
                                                @AuthenticationPrincipal @ApiIgnore ConsumerEntity consumer) {
        review = new ReviewBuilder(review)
                .setProvider(getProviderById(idProvider))
                .setConsumer(getConsumerById(consumer.getId()))
                .setJob(getJobById(idJob))
                .setRecievedByProvider(true).build();
        return reviewRepository.save(review);
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @PostMapping("/insert/{job}/consumer/{id}")
    public ReviewEntity insertReviewForConsumer(@PathVariable("job") final Integer idJob,
                                                @PathVariable("id") final Integer idConsumer,
                                                @RequestBody @Valid ReviewEntity review,
                                                @AuthenticationPrincipal @ApiIgnore ProviderEntity provider) {
        review = new ReviewBuilder(review)
                .setProvider(getProviderById(provider.getId()))
                .setConsumer(getConsumerById(idConsumer))
                .setJob(getJobById(idJob))
                .setRecievedByProvider(false).build();
        return reviewRepository.save(review);
    }

    @ResponseBody
    @Secured("ROLE_PROVIDER")
    @PostMapping("/update/consumer/{id}")
    public ResponseEntity<ReviewEntity> updateReviewForConsumer(@PathVariable("id") final Integer id,
                                                                @RequestParam(required = false, value = "rating") final Integer rating,
                                                                @RequestParam(required = false, value = "message") final String message) {
        return updateReviewBasedOn(id, rating, message, reviewRepository::updateReviewForConsumer);
    }

    @ResponseBody
    @Secured("ROLE_CONSUMER")
    @PostMapping("/update/provider/{id}")
    public ResponseEntity<ReviewEntity> updateReviewForProvider(@PathVariable("id") final Integer id,
                                                                @RequestParam(required = false, value = "rating") final Integer rating,
                                                                @RequestParam(required = false, value = "message") final String message) {
        return updateReviewBasedOn(id, rating, message, reviewRepository::updateReviewForProvider);
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    @GetMapping("/latest/consumer/{id}/{limit}/{received}")
    public List<ReviewEntity> getLatestReviewsConsumer(@PathVariable("id") final Integer id,
                                                       @PathVariable("limit") final Integer limit,
                                                       @PathVariable("received") final Boolean received) throws NotFoundException {
        return reviewRepository.findLatestReviewsConsumer(getConsumerById(id), received, new PageRequest(0, limit));
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    @GetMapping("/latest/provider/{id}/{limit}/{received}")
    public List<ReviewEntity> getLatestReviewsProvider(@PathVariable("id") final Integer id,
                                                       @PathVariable("limit") final Integer limit,
                                                       @PathVariable("received") final Boolean received) throws NotFoundException {
        return reviewRepository.findLatestReviewsProvider(getProviderById(id), received, new PageRequest(0, limit));
    }

    private ResponseEntity<ReviewEntity> updateReviewBasedOn(final Integer id, final Integer rating, final String message,
                                                             final TriFunction<Integer, Integer, String, Integer> getter) {
        ReviewEntity review = getReviewById(id);
        Integer finalRating = rating != null ? rating : review.getRating();
        String finalMessage = message != null ? message : review.getMessage();
        return getter.apply(id, finalRating, finalMessage) > 0 ?
                new ResponseEntity<>(getReviewById(id), HttpStatus.ACCEPTED) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private JobEntity getJobById(final Integer id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found"));
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
