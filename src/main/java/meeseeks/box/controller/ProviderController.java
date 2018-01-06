package meeseeks.box.controller;

import meeseeks.box.domain.*;
import meeseeks.box.exception.DataAlreadyExists;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.PusherService;
import meeseeks.box.service.UserService;
import meeseeks.box.service.PusherService.NotificationType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RestController
@RequestMapping("/provider")
public class ProviderController {

    private final ProviderRepository providerRepository;
    private final UserService userService;
    private final SecurityConstants securityConstants;
    private final JobRepository jobRepository;
    private final RequestRepository requestRepository;
    private final PusherService pusherService;

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ProviderController(final ProviderRepository providerRepository,
                              final UserService userService,
                              final SecurityConstants securityConstants,
                              final JobRepository jobRepository,
                              final RequestRepository requestRepository,
                              final PusherService pusherService) {
        this.providerRepository = providerRepository;
        this.userService = userService;
        this.securityConstants = securityConstants;
        this.jobRepository = jobRepository;
        this.requestRepository = requestRepository;
        this.pusherService = pusherService;
    }

    @ResponseBody
    @GetMapping("/get/{id}")
    public ProviderEntity getProviderById(@PathVariable("id") final Integer id) throws NotFoundException {
        return providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider Not Found!"));
    }

    @ResponseBody
    @GetMapping("/get/{id}/skills")
    public List<SkillEntity> getProviderSkillsById(@PathVariable("id") final Integer id) throws NotFoundException {
        return providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider Not Found!")).getSkills();
    }

    @PostMapping("/register")
    public void registerProvider(@RequestBody @Validated(UserEntity.ValidationRegister.class) ProviderEntity provider) {
        LOGGER.info("Provider " + provider.getUsername() + " registers now ...");
        userService.saveUser(provider);
    }

    @Secured({"ROLE_PROVIDER"})
    @PatchMapping("/update")
    public void editConsumer(@RequestBody @Validated(UserEntity.ValidationEdit.class)
                                     ProviderEntity provider, Authentication auth, HttpServletResponse response) {
        ProviderEntity oldProvider = (ProviderEntity) auth.getPrincipal();
        if (provider.getEmail() != null && !provider.getEmail().isEmpty()) {
            oldProvider.setEmail(provider.getEmail());
        }
        if (provider.getName() != null && !provider.getName().isEmpty()) {
            oldProvider.setName(provider.getName());
        }
        if (provider.getUsername() != null && !provider.getUsername().isEmpty()) {
            oldProvider.setUsername(provider.getUsername());
        }
        if (provider.getProfileImageUrl() != null && !provider.getProfileImageUrl().isEmpty()) {
            oldProvider.setProfileImageUrl(provider.getProfileImageUrl());
        }
        if (provider.getDescription() != null && !provider.getDescription().isEmpty()) {
            oldProvider.setDescription(provider.getDescription());
        }
        if (provider.getProfileVideoUrl() != null && !provider.getProfileVideoUrl().isEmpty()) {
            oldProvider.setProfileVideoUrl(provider.getProfileVideoUrl());
        }
        providerRepository.save(oldProvider);
        response.addHeader(securityConstants.HEADER_STRING,
                securityConstants.TOKEN_PREFIX + userService.getJWTToken(oldProvider)
        );
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @PostMapping("/apply/{jobId}/{message}")
    public ResponseEntity<RequestEntity> applyToJob(@AuthenticationPrincipal @ApiIgnore ProviderEntity provider,
                                                    @PathVariable(value = "jobId") final Integer id,
                                                    @PathVariable(value = "message") final String message) throws NotFoundException, DataAlreadyExists {
        JobEntity job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("404 Job Not Found!"));
        requestRepository.getRequestByProviderAndJob(provider, job).ifPresent(item -> {
            throw new DataAlreadyExists("Provider's application already exists!");
        });
        Optional<ProviderEntity> providerById = providerRepository.findById(provider.getId());
        ProviderEntity theProvider = providerById.orElseGet(() -> providerById.orElseThrow(() -> new NotFoundException("Provider Not Found!")));
        RequestEntity request = new RequestEntity(theProvider, job, message);

        ResponseEntity<RequestEntity> response = new ResponseEntity<>(requestRepository.save(request), HttpStatus.ACCEPTED);

        // Send a new notification to the consumer using Pusher Feeds
        HashMap<String, Object> data = new HashMap<>();
        data.put("job", job.getId());
        data.put("provider", provider.getName());
        pusherService.createNotification(NotificationType.JOB_APPLY, job.getConsumer().getId(), data);

        return response;
    }
}
