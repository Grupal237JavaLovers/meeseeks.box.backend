package meeseeks.box.controller;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.model.JobModel;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by nicof on 10/28/2017.
 */

@RestController
@RequestMapping("/job")
public class JobController {

    private final JobRepository jobRepository;
    private final ProviderRepository providerRepository;
    private final ConsumerRepository consumerRepository;

    @Autowired
    public JobController(final JobRepository jobRepository,
                         final ProviderRepository providerRepository,
                         final ConsumerRepository consumerRepository) {
        this.jobRepository = jobRepository;
        this.providerRepository = providerRepository;
        this.consumerRepository = consumerRepository;
    }

    // TODO: Test
    @ResponseBody
    @RequestMapping("/insert")
    public JobEntity insert(@RequestBody JobModel job) {
        return jobRepository.save(job.build());
    }

    // TODO: Test
    @Secured({"ROLE_CONSUMER"})
    @RequestMapping("/delete/id")
    public ResponseEntity delete(@PathVariable("id") final Integer id,
                                 final Authentication authentication) {
        ConsumerEntity consumer = (ConsumerEntity) authentication.getPrincipal();
        return jobRepository.deleteIfCreatedBy(consumer.getId(), id) > 0 ?
                new ResponseEntity<>(HttpStatus.ACCEPTED) :
                new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    // TODO: Test
    @Secured({"ROLE_CONSUMER"})
    @RequestMapping("/update")
    public JobEntity update(@RequestBody JobModel updated,
                            final Authentication authentication) {
        ConsumerEntity consumer = (ConsumerEntity) authentication.getPrincipal();
        jobRepository.updateIfCreatedBy(consumer.getId(), updated.getJob().getId(), updated.build());
        return jobRepository.findById(updated.getJob().getId())
                .orElseThrow(() -> new NotFoundException("Job Not Found"));
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
