package meeseeks.box.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;

/**
 * Created by nicof on 10/28/2017.
 */
@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobRepository jobRepo;
    private final ProviderRepository providerRepo;
    private final ConsumerRepository consumerRepo;

    @Autowired
    public JobController(JobRepository jobRepo, ProviderRepository providerRepo, ConsumerRepository consumerRepo) {
        this.jobRepo = jobRepo;
        this.providerRepo = providerRepo;
        this.consumerRepo = consumerRepo;
    }

    @RequestMapping("/latest/provider/{id}/{nr}")
    public List<JobEntity> getLatestByProvider(@PathVariable("id") final Integer id, @PathVariable("nr") final Integer nr) throws NotFoundException{
        ProviderEntity provider = providerRepo.findById(id).orElseThrow(() -> {
            return new NotFoundException("Provider not found");
        });

        return jobRepo.findLatestJobByProvider(provider, new PageRequest(0, nr));
    }

    @RequestMapping("/latest/consumer/{id}/{nr}")
    public List<JobEntity> getLatestByConsumer(@PathVariable("id") final Integer id, @PathVariable("nr") final Integer nr) throws NotFoundException{
        ConsumerEntity consumer = consumerRepo.findById(id).orElseThrow(() -> {
            return new NotFoundException("Consumer not found");
        });

        return jobRepo.findLatestJobByConsumer(consumer, new PageRequest(0, nr));
    }

}
