package meeseeks.box.controller;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by nicof on 10/28/2017.
 */
@RestController
@RequestMapping("/jobs")
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

    @ResponseBody
    @RequestMapping("/latest/provider/{id}/{limit}")
    public List<JobEntity> getLatestByProvider(@PathVariable("id") final Integer id,
                                               @PathVariable("limit") final Integer limit) throws NotFoundException {
        return jobRepository.findLatestJobByProvider(getProviderById(id), new PageRequest(0, limit));
    }

    @ResponseBody
    @RequestMapping("/latest/consumer/{id}/{limit}")
    public List<JobEntity> getLatestByConsumer(@PathVariable("id") final Integer id,
                                               @PathVariable("limit") final Integer limit) throws NotFoundException {
        return jobRepository.findLatestJobByConsumer(getConsumerById(id), new PageRequest(0, limit));
    }

    private ProviderEntity getProviderById(final Integer id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider not found"));
    }

    private ConsumerEntity getConsumerById(final Integer id) {
        return consumerRepository.findById(id).orElseThrow(() -> new NotFoundException("Consumer not found"));
    }
}
