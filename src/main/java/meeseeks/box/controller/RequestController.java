package meeseeks.box.controller;

import meeseeks.box.domain.RequestEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.model.DateRange;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestController {

    private final RequestRepository requestRepository;
    private final JobRepository jobRepository;
    private final ProviderRepository providerRepository;

    @Autowired
    public RequestController(
            final RequestRepository requestRepository,
            final JobRepository jobRepository,
            final ProviderRepository providerRepository) {
        this.requestRepository = requestRepository;
        this.jobRepository = jobRepository;
        this.providerRepository = providerRepository;
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<RequestEntity> delete(@PathVariable("id") Integer id) {
        return requestRepository.deleteRequestFromCurrentProvider(id) > 0 ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @Secured("ROLE_PROVIDER")
    @PatchMapping("/update/{id}/{message}")
    public ResponseEntity<RequestEntity> update(
            @PathVariable("id") Integer id,
            @PathVariable("message") final String message) {
        return requestRepository.updateRequestFromCurrentProvider(id, message) > 0 ?
                new ResponseEntity<>(requestRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Request Not Found!")), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @GetMapping("/get/{id}")
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    public RequestEntity getRequestById(@PathVariable("id") Integer id) {
        return findRequestById(id);
    }

    @ResponseBody
    @GetMapping("/find/between/{limit}")
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    public List<RequestEntity> findRequestsWithDateInRange(
            @RequestBody DateRange range,
            @PathVariable("limit") Integer limit) {
        return requestRepository.findByDateBetween(range.getStart(), range.getEnd(),
                new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/latest/job/{idJob}/{limit}")
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    public List<RequestEntity> getLatestRequestsFromJob(
            @PathVariable("idJob") Integer id,
            @PathVariable("limit") Integer limit) {
        return requestRepository.findLatestRequestsFromJob(jobRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Job Not Found!")),
                new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/latest/provider/{idProvider}/{limit}")
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    public List<RequestEntity> getLatestRequestsForProvider(
            @PathVariable("idProvider") Integer id,
            @PathVariable("limit") Integer limit) {
        return requestRepository.findLatestRequestsForProvider(
                providerRepository.findById(id).orElseThrow(() ->
                        new NotFoundException("Provider Not Found")),
                new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/latest/provider/accepted/{idProvider}/{limit}")
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    public List<RequestEntity> getLatestAcceptedRequestsForProvider(
            @PathVariable("idProvider") Integer id,
            @PathVariable("limit") Integer limit) {
        return requestRepository.findLatestAcceptedRequestsForProvider(providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider Not Found!")), new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/get/all/{limit}")
    @Secured({"ROLE_PROVIDER"})
    public List<RequestEntity> getAllRequestsForCurrentProvider(
            @PathVariable("limit") final Integer limit) {
        return requestRepository.getRequestsForCurrentProvider(new PageRequest(0, limit));
    }

    private RequestEntity findRequestById(final Integer id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request Not Found!"));
    }
}
