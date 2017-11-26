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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by nicof on 10/28/2017.
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
    public JobController(final JobRepository jobRepository,
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

    @Secured({"ROLE_CONSUMER"})
    @ResponseBody
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
    public ResponseEntity<?> delete(@PathVariable("id") final Integer id,
                                    @AuthenticationPrincipal @ApiIgnore ConsumerEntity consumer) {
        return jobRepository.deleteIfCreatedBy(consumer, id) > 0 ?
                new ResponseEntity<>(HttpStatus.ACCEPTED) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Secured({"ROLE_CONSUMER"})
    @PatchMapping("/update")
    public ResponseEntity<JobEntity> update(@RequestBody JobModel updated,
                                            @AuthenticationPrincipal @ApiIgnore ConsumerEntity consumer) {
        return jobRepository.updateIfCreatedBy(consumer.getId(), updated.getJob().getId(), updated.build(consumer)) > 0 ?
                new ResponseEntity<>(jobRepository.findById(updated.getJob().getId())
                        .orElseThrow(() -> new NotFoundException("Updated Job Not Found!")), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Secured({"ROLE_PROVIDER"})
    @ResponseBody
    @GetMapping("/latest/provider/{limit}")
    public List<JobEntity> getLatestJobsRequestedByProvider(@AuthenticationPrincipal @ApiIgnore ProviderEntity provider,
                                                            @PathVariable("limit") final Integer limit)  {
        return jobRepository.findLatestJobsRequestedByProvider(provider, new PageRequest(0, limit));
    }

    @Secured({"ROLE_CONSUMER"})
    @ResponseBody
    @GetMapping("/latest/consumer/{limit}")
    public List<JobEntity> getLatestJobsCreatedByConsumer(@AuthenticationPrincipal @ApiIgnore ConsumerEntity consumer,
                                                          @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestJobsCreatedByConsumer(consumer, new PageRequest(0, limit));
    }

    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @ResponseBody
    @GetMapping("/find/location/{location}/{limit}")
    public List<JobEntity> getLatestJobsByLocation(@PathVariable("location") final String location,
                                                   @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestByLocation(location, new PageRequest(0, limit));
    }

    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @ResponseBody
    @GetMapping("/find/category/{limit}")
    public List<JobEntity> getLatestJobsByCategory(@RequestBody final CategoryEntity category,
                                                   @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestByCategory(category, new PageRequest(0, limit));
    }

    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @ResponseBody
    @GetMapping("/find/price_between/{low}/{high}/{limit}")
    public List<JobEntity> getLatestJobByPriceBetween(@PathVariable("low") final Double low,
                                                      @PathVariable("high") final Double high,
                                                      @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestByPriceBetween(low, high, new PageRequest(0, limit));
    }

    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @ResponseBody
    @GetMapping("/find/expiration_before/{date}/{limit}")
    public List<JobEntity> getLatestJobsByExpirationDateBefore(@PathVariable("date") final Calendar date,
                                                               @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestByExpirationBefore(date, new PageRequest(0, limit));
    }

    @Secured({"ROLE_CONSUMER", "ROLE_PROVIDER"})
    @ResponseBody
    @GetMapping("/find/type/{type}/{limit}")
    public List<JobEntity> getLatestJobsByType(@PathVariable("type") final String type,
                                               @PathVariable("limit") final Integer limit) {
        return jobRepository.findLatestByType(type, new PageRequest(0, limit));
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
