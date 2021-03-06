package meeseeks.box.controller;

import meeseeks.box.domain.*;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.model.JobModel;
import meeseeks.box.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicoleta Fecioru
 * @version 1.0
 */

@RestController
@RequestMapping("/job")
public class JobController {

    private final JobRepository jobRepository;
    private final ProviderRepository providerRepository;
    private final ConsumerRepository consumerRepository;
    private final CategoryRepository categoryRepository;
    private final AvailabilityRepository availabilityRepository;

    @Autowired
    public JobController(
            final JobRepository jobRepository,
            final ProviderRepository providerRepository,
            final ConsumerRepository consumerRepository,
            final CategoryRepository categoryRepository,
            final AvailabilityRepository availabilityRepository) {
        this.jobRepository = jobRepository;
        this.providerRepository = providerRepository;
        this.consumerRepository = consumerRepository;
        this.categoryRepository = categoryRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @ResponseBody
    @Secured({"ROLE_CONSUMER"})
    @PostMapping("/insert")
    public JobEntity insert(@RequestBody @Valid JobModel job) {
        ConsumerEntity consumer = (ConsumerEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Get the managed consumer from the database because Hibernate weirdness
        consumer = consumerRepository.findOne(consumer.getId());
        // Link to existing category
        job.setCategory(categoryRepository.findByName(job.getCategory().getName()).orElse(job.getCategory()));
        // Link to existing availabilities
        List<AvailabilityEntity> availabilities = new ArrayList<>();
        for (AvailabilityEntity entity : job.getAvailabilities()) {
            availabilities.add(availabilityRepository.findByDayAndStartHourAndEndHour(entity.getDay(), entity.getStartHour(), entity.getEndHour()).orElse(entity));
        }
        job.setAvailabilities(availabilities);
        return jobRepository.save(job.build(consumer));
    }

    @Secured({"ROLE_CONSUMER"})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("id") final Integer id,
            @AuthenticationPrincipal @ApiIgnore ConsumerEntity consumer) {
        return jobRepository.deleteIfCreatedBy(consumer, id) > 0 ?
                new ResponseEntity<>(HttpStatus.ACCEPTED) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Secured({"ROLE_CONSUMER"})
    @PatchMapping("/update")
    public ResponseEntity<JobEntity> update(
            @RequestBody JobModel job,
            @AuthenticationPrincipal @ApiIgnore ConsumerEntity consumer) {
        return getConsumerFromDatabaseForJob(job.getJob()).getId().equals(consumer.getId()) ?
                new ResponseEntity<>(jobRepository.save(job.build(consumer)),
                        HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private ConsumerEntity getConsumerFromDatabaseForJob(final JobEntity job) {
        return consumerRepository.findByJobsIsContaining(job)
                .orElseThrow(() -> new NotFoundException("404 Consumer Not Found!"));
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @GetMapping("/latest/provider/{limit}")
    public List<JobEntity> getLatestJobsRequestedByProvider(
            @AuthenticationPrincipal @ApiIgnore ProviderEntity provider,
            @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestJobsRequestedByProvider(
                provider, new PageRequest(0, limit));
    }

    @ResponseBody
    @Secured({"ROLE_CONSUMER"})
    @GetMapping("/latest/consumer/{limit}")
    public List<JobEntity> getLatestJobsCreatedByConsumer
            (@AuthenticationPrincipal @ApiIgnore ConsumerEntity consumer,
             @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestJobsCreatedByConsumer(
                consumer, new PageRequest(0, limit));
    }

    @ResponseBody
    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @GetMapping("/{id}")
    public JobEntity getJob(@PathVariable("id") final Integer id) {
        return jobRepository.findOne(id);
    }

    @ResponseBody
    @GetMapping("/categories")
    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    public Iterable<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    @ResponseBody
    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @GetMapping("/find/location/{location}/{limit}")
    public List<JobEntity> getLatestJobsByLocation(
            @PathVariable("location") final String location,
            @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestByLocation(location,
                new PageRequest(0, limit));
    }

    @ResponseBody
    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @GetMapping("/find/category/{limit}")
    public List<JobEntity> getLatestJobsByCategory(
            @RequestBody final CategoryEntity category,
            @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestByCategory(category,
                new PageRequest(0, limit));
    }

    @ResponseBody
    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @GetMapping("/find/price_between/{low}/{high}/{limit}")
    public List<JobEntity> getLatestJobByPriceBetween(
            @PathVariable("low") final Double low,
            @PathVariable("high") final Double high,
            @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestByPriceBetween(
                low, high, new PageRequest(0, limit));
    }

    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @ResponseBody
    @GetMapping("/search/{criteria}/{limit}")
    public List<JobEntity> getAllJobsBySearchCriteria(
            @PathVariable("criteria") final String criteria,
            @PathVariable("limit") final Integer limit) {
        if (criteria.equals("null")) {
            return jobRepository.findAllByNameContaining("", new PageRequest(0, limit));
        }
        return jobRepository.findAllByNameContaining(criteria, new PageRequest(0, limit));
    }

    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @ResponseBody
    @GetMapping("/find/type/{type}/{limit}")
    public List<JobEntity> getLatestJobsByType(@PathVariable("type") final String type,
                                               @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestByType(type, new PageRequest(0, limit));
    }
}
