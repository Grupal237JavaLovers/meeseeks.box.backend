package meeseeks.box.controller;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.model.JobModel;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by nicof on 10/28/2017.
 */
@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobRepository jobRepository;
    private final RequestRepository requestRepository;
    private final ProviderRepository providerRepository;
    private final ConsumerRepository consumerRepository;

    @Autowired
    public JobController(final JobRepository jobRepository,
                         final RequestRepository requestRepository,
                         final ProviderRepository providerRepository,
                         final ConsumerRepository consumerRepository) {
        this.jobRepository = jobRepository;
        this.requestRepository = requestRepository;
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
    @RequestMapping("/delete/id")
    public ResponseEntity delete(@PathVariable("id") final Integer id) throws NotFoundException {
        jobRepository.delete(getJobById(id));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // TODO: Test
    @RequestMapping("/update")
    public JobEntity update(@RequestBody JobModel updated) {
        return jobRepository.save(updated.build());
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

    private JobEntity getJobById(final Integer id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job Not Found"));
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
