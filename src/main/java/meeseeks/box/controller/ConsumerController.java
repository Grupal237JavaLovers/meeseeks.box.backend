package meeseeks.box.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.RequestEntity;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.PusherService;
import meeseeks.box.service.PusherService.NotificationType;
import meeseeks.box.service.UserService;
import springfox.documentation.annotations.ApiIgnore;


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
    private final PusherService pusherService;

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ConsumerController(final ConsumerRepository consumerRepository,
                              final UserService userService,
                              final SecurityConstants securityConstants,
                              final JobRepository jobRepository,
                              final ProviderRepository providerRepository,
                              final RequestRepository requestRepository,
                              final PusherService pusherService) {
        this.consumerRepository = consumerRepository;
        this.userService = userService;
        this.securityConstants = securityConstants;
        this.jobRepository = jobRepository;
        this.providerRepository = providerRepository;
        this.requestRepository = requestRepository;
        this.pusherService = pusherService;
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
    @PostMapping("/accept/{idJob}/{idProvider}")
    public ResponseEntity<?> acceptProviderForJob(@PathVariable("idJob") final Integer idJob,
                                                  @PathVariable("idProvider") final Integer idProvider,
                                                  @AuthenticationPrincipal @ApiIgnore ConsumerEntity consumer) {
        JobEntity job = jobRepository.findById(idJob).
                orElseThrow(() -> new NotFoundException("Job Not Found!"));
        ProviderEntity provider = providerRepository.findById(idProvider)
                .orElseThrow(() -> new NotFoundException("Provider Not Found"));
        RequestEntity request = requestRepository.getRequestByProviderAndJob(provider, job)
                .orElseThrow(() -> new NotFoundException("Request Not Found!"));
        request.setAccepted(Boolean.TRUE);
        ResponseEntity<?> response = job.getConsumer().getId().equals(consumer.getId()) ?
                new ResponseEntity<>(requestRepository.save(request), HttpStatus.ACCEPTED) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            // Send a new notification to the provider using Pusher Feeds
            HashMap<String, Object> data = new HashMap<>();
            data.put("job", job.getId());
            data.put("consumer", consumer.getName());
            pusherService.createNotification(NotificationType.JOB_APPLY, provider.getId(), data);
        }

        return response;
    }
}
