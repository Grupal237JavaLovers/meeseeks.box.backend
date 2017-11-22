package meeseeks.box.controller;

import meeseeks.box.domain.*;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


/**
 * @author Tiron Andreea-Ecaterina
 * @version 1.0
 */

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    private final ConsumerRepository consumerRepository;
    private final UserService userService;
    private final SecurityConstants securityConstants;
    private final JobRepository jobRepository;
    private final ProviderRepository providerRepository;
    private final RequestRepository requestRepository;

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ConsumerController(final ConsumerRepository consumerRepository,
                              final UserService userService,
                              final SecurityConstants securityConstants,
                              final JobRepository jobRepository,
                              final ProviderRepository providerRepository,
                              final RequestRepository requestRepository) {
        this.consumerRepository = consumerRepository;
        this.userService = userService;
        this.securityConstants = securityConstants;
        this.jobRepository = jobRepository;
        this.providerRepository = providerRepository;
        this.requestRepository = requestRepository;
    }

    @GetMapping("/get/{id}")
    public @ResponseBody
    ConsumerEntity getConsumerById(@PathVariable("id") final Integer id) throws NotFoundException {
        return consumerRepository.findById(id).orElseThrow(() -> new NotFoundException("Consumer not found"));
    }

    @PostMapping("/register")
    public void registerConsumer(@RequestBody @Validated(UserEntity.ValidationRegister.class) ConsumerEntity consumer) {
        LOGGER.info("Consumer " + consumer.getUsername() + " registers now ...");
        userService.saveUser(consumer);
    }

    @Secured({"ROLE_CONSUMER"})
    @PatchMapping("/update")
    public void updateConsumer(@RequestBody @Validated(UserEntity.ValidationEdit.class) final ConsumerEntity consumer,
                               final Authentication authentication,
                               final HttpServletResponse response) {
        ConsumerEntity oldConsumer = (ConsumerEntity) authentication.getPrincipal();
        if (consumer.getEmail() != null && !consumer.getEmail().isEmpty()) {
            oldConsumer.setEmail(consumer.getEmail());
        }
        if (consumer.getName() != null && !consumer.getName().isEmpty()) {
            oldConsumer.setName(consumer.getName());
        }
        if (consumer.getUsername() != null && !consumer.getUsername().isEmpty()) {
            oldConsumer.setUsername(consumer.getUsername());
        }
        if (consumer.getProfileImageUrl() != null && !consumer.getProfileImageUrl().isEmpty()) {
            oldConsumer.setProfileImageUrl(consumer.getProfileImageUrl());
        }
        consumerRepository.save(oldConsumer);
        response.addHeader(securityConstants.HEADER_STRING,
                securityConstants.TOKEN_PREFIX + userService.getJWTToken(oldConsumer)
        );
    }

    @Secured({"ROLE_CONSUMER"})
    @ResponseBody
    @PutMapping("/accept/{idJob}/{idProvider}")
    public ResponseEntity<RequestEntity> acceptProviderForJob(@PathVariable("idJob") final Integer idJob,
                                                              @PathVariable("idProvider") final Integer idProvider,
                                                              @AuthenticationPrincipal ConsumerEntity consumer) {
        JobEntity job = jobRepository.findById(idJob).
                orElseThrow(() -> new NotFoundException("Job Not Found!"));
        ProviderEntity provider = providerRepository.findById(idProvider)
                .orElseThrow(() -> new NotFoundException("Provider Not Found"));
        RequestEntity request = requestRepository.getRequestByProviderAndJob(provider, job)
                .orElseThrow(() -> new NotFoundException("Request Not Found!"));
        request.setAccepted(Boolean.TRUE);
        return job.getConsumer().equals(consumer) ?
                new ResponseEntity<>(requestRepository.save(request), HttpStatus.ACCEPTED) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
