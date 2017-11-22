package meeseeks.box.controller;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.UserService;

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

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ProviderController(final ProviderRepository providerRepository,
            final UserService userService,
            final SecurityConstants securityConstants) {
        this.providerRepository = providerRepository;
        this.userService = userService;
        this.securityConstants = securityConstants;
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
}
