package meeseeks.box.controller;

import meeseeks.box.domain.*;
import meeseeks.box.exception.DataAlreadyExists;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

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

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ProviderController(final ProviderRepository providerRepository,
                              final UserService userService,
                              final SecurityConstants securityConstants,
                              final JobRepository jobRepository,
                              final RequestRepository requestRepository) {
        this.providerRepository = providerRepository;
        this.userService = userService;
        this.securityConstants = securityConstants;
        this.jobRepository = jobRepository;
        this.requestRepository = requestRepository;
    }

    @ResponseBody
    @GetMapping("/get/{id}")
    public ProviderEntity getProviderById(@PathVariable("id") final Integer id) throws NotFoundException {
        return providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider Not Found!"));
    }

    @ResponseBody
    @GetMapping("/get/{id}/skills")
    public Set<SkillEntity> getProviderSkillsById(@PathVariable("id") final Integer id) throws NotFoundException {
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
    @PutMapping("/apply/{jobId}/{message}")
    public RequestEntity applyToJob(@AuthenticationPrincipal ProviderEntity provider,
                                   @PathVariable(value = "message") final String message,
                                   @PathVariable(value = "jobId") final Integer id) throws NotFoundException, DataAlreadyExists {
        JobEntity job = jobRepository.findById(id).orElseThrow(() -> new NotFoundException("404 Job Not Found!"));
        requestRepository.getRequestByProviderAndJob(provider, job)
                .orElseThrow(() -> new DataAlreadyExists("Provider's application already exists!"));
        RequestEntity request = new RequestEntity(provider, job, message);
        return requestRepository.save(request);
    }
}
