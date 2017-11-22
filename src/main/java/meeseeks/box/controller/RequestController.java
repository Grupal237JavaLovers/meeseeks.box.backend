package meeseeks.box.controller;

import meeseeks.box.domain.ProviderEntity;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestController {

    private final RequestRepository requestRepository;
    private final JobRepository jobRepository;
    private final ProviderRepository providerRepository;

    @Autowired
    public RequestController(final RequestRepository requestRepository,
                             final JobRepository jobRepository,
                             final ProviderRepository providerRepository) {
        this.requestRepository = requestRepository;
        this.jobRepository = jobRepository;
        this.providerRepository = providerRepository;
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @PostMapping("/insert")
    public ResponseEntity<RequestEntity> insert(@Valid @RequestBody RequestEntity request,
                                                final Authentication authentication) {
        ProviderEntity user = (ProviderEntity) authentication.getPrincipal();
        return user.equals(request.getProvider()) ?
                new ResponseEntity<>(requestRepository.save(request), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<RequestEntity> delete(@PathVariable("id") Integer id,
                                                final Authentication authentication) {
        ProviderEntity user = (ProviderEntity) authentication.getPrincipal();
        RequestEntity request = findRequestById(id);
        if (user.equals(request.getProvider())) {
            requestRepository.delete(request);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @PatchMapping("/update")
    public ResponseEntity<RequestEntity> update(@Valid @RequestBody RequestEntity updated,
                                                final Authentication authentication) {
        ProviderEntity user = (ProviderEntity) authentication.getPrincipal();
        return user.equals(updated.getProvider()) ?
                new ResponseEntity<>(requestRepository.save(updated), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @GetMapping("/get/{id}")
    public RequestEntity getRequestById(@PathVariable("id") Integer id) {
        return findRequestById(id);
    }

    @ResponseBody
    @GetMapping("/find/between/{limit}")
    public List<RequestEntity> findRequestsWithDateInRange(@RequestBody DateRange range,
                                                           @PathVariable("limit") Integer limit) {
        return requestRepository.findByDateBetween(range.getStart(), range.getEnd(), new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/latest/job/{idJob}/{limit}")
    public List<RequestEntity> getLatestRequestsFromJob(@PathVariable("idJob") Integer id,
                                                        @PathVariable("limit") Integer limit) {
        return requestRepository.findLatestRequestsFromJob(jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job Not Found!")), new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/latest/provider/{idProvider}/{limit}")
    public List<RequestEntity> getLatestRequestsForProvider(@PathVariable("idProvider") Integer id,
                                                            @PathVariable("limit") Integer limit) {
        return requestRepository.findLatestRequestsForProvider(providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider Not Found")), new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/latest/provider/accepted/{idProvider}/{limit}")
    public List<RequestEntity> getLatestAcceptedRequestsForProvider(@PathVariable("idProvider") Integer id,
                                                                    @PathVariable("limit") Integer limit) {
        return requestRepository.findLatestAcceptedRequestsForProvider(providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider Not Found!")), new PageRequest(0, limit));
    }

    private RequestEntity findRequestById(@PathVariable("id") Integer id) {
        return requestRepository.findById(id).orElseThrow(() -> new NotFoundException("Request Not Found!"));
    }
}
