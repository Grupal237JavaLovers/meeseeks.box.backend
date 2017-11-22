package meeseeks.box.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import meeseeks.box.domain.AvailabilityEntity;
import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.model.JobModel;
import meeseeks.box.repository.AvailabilityRepository;
import meeseeks.box.repository.CategoryRepository;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;

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
    @RequestMapping(value = "/insert", method={RequestMethod.POST})
    @Transactional
    public JobEntity insert(@RequestBody @Valid JobModel job) {
         ConsumerEntity consumer = (ConsumerEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Get the managed consumer from the database because Hibernate weirdness
        consumer = consumerRepository.findOne(consumer.getId());

        // Link to existing category
        job.setCategory(categoryRepository.findByName(job.getCategory().getName()).orElse(job.getCategory()));

        // Link to existing availabilities
        Set<AvailabilityEntity> availabilities = new HashSet<>();
        for (AvailabilityEntity entity : job.getAvailabilities()) {
            availabilities.add(availabilityRepository.findByDayAndStartHourAndEndHour(entity.getDay(), entity.getStartHour(), entity.getEndHour()).orElse(entity));
        }

        job.setAvailabilities(availabilities);

        return jobRepository.save(job.build(consumer));
    }

    @Secured({"ROLE_CONSUMER"})
    @RequestMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") final Integer id,
                                 final Authentication authentication) {
        ConsumerEntity consumer = (ConsumerEntity) authentication.getPrincipal();
        return jobRepository.deleteIfCreatedBy(consumer.getId(), id) > 0 ?
                new ResponseEntity<>(HttpStatus.ACCEPTED) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Secured({"ROLE_CONSUMER"})
    @RequestMapping("/update")
    public ResponseEntity<JobEntity> update(@RequestBody JobModel updated,
                                            final Authentication authentication) {
        ConsumerEntity consumer = (ConsumerEntity) authentication.getPrincipal();
        return jobRepository.updateIfCreatedBy(consumer.getId(), updated.getJob().getId(), updated.build(consumer)) > 0 ?
                new ResponseEntity<>(jobRepository.findById(updated.getJob().getId())
                        .orElseThrow(() -> new NotFoundException("Updated Job Not Found!")), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @RequestMapping("/latest/provider/{id}/{limit}")
    public List<JobEntity> getLatestJobsRequestedByProvider(@PathVariable("id") final Integer id,
                                                            @PathVariable("limit") final Integer limit) throws NotFoundException {
        return jobRepository.findLatestJobsRequestedByProvider(getProviderById(id), new PageRequest(0, limit));
    }

    @ResponseBody
    @RequestMapping("/latest/consumer/{id}/{limit}")
    public List<JobEntity> getLatestJobsCreatedByConsumer(@PathVariable("id") final Integer id,
                                                          @PathVariable("limit") final Integer limit) throws NotFoundException {
        return jobRepository.findLatestJobsCreatedByConsumer(getConsumerById(id), new PageRequest(0, limit));
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
