package meeseeks.box.controller;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.ReviewEntity;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicof on 10/28/2017.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/jobs")
public class JobController {
    @Autowired
    private JobRepository repo;
    @Autowired
    private ProviderRepository repoProv;
    @Autowired
    private ConsumerRepository repoConsumer;


    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody JobEntity get(@PathVariable("id") final Integer id) {
        return repo.findOne(id);
    }

    @RequestMapping("/latest/provider/{id}/{nr}")
    public List<JobEntity> getLatestByProvider(@PathVariable("id") final Integer id,@PathVariable("nr") final Integer nr){
        ProviderEntity p = repoProv.findOne(id);
        List<JobEntity> latestJobByProvider = repo.findLatestJobByProvider(p);
        if (latestJobByProvider.size()>nr) {
            return latestJobByProvider.subList(0, nr);
        } else {
            return latestJobByProvider;
        }
    }

    @RequestMapping("/latest/consumer/{id}/{nr}")
    public List<JobEntity> getLatestByConsumer(@PathVariable("id") final Integer id,@PathVariable("nr") final Integer nr){
        ConsumerEntity c = repoConsumer.findOne(id);
        List<JobEntity> latestJobByProvider = repo.findLatestJobByConsumer(c);
        if (latestJobByProvider.size()>nr) {
            return latestJobByProvider.subList(0, nr);
        } else {
            return latestJobByProvider;
        }
    }


    @RequestMapping(value = "/reviews/provider/{id}/{nr}", method = RequestMethod.GET)
    public @ResponseBody List<ReviewEntity> getTopReviewsForProvider(@PathVariable("id") final Integer id, @PathVariable("nr") final Integer nr) {
        ProviderEntity provider = repoProv.findOne(id);
        List<ReviewEntity> reviews = new ArrayList<>(provider.getReviews());
        reviews.sort((r1, r2) -> r2.getRating() - r1.getRating());

        return reviews.subList(0, nr);
    }

    @RequestMapping(value = "/reviews/consumer/{id}/{nr}", method = RequestMethod.GET)
    public @ResponseBody List<ReviewEntity> getTopReviewsForConsumer(@PathVariable("id") final Integer id, @PathVariable("nr") final Integer nr) {
        ConsumerEntity consumer = repoConsumer.findOne(id);
        List<ReviewEntity> reviews = new ArrayList<>(consumer.getReviews());
        reviews.sort((r1, r2) -> r2.getRating() - r1.getRating());

        return reviews.subList(0, nr);
    }
}


