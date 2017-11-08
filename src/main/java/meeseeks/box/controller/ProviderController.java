package meeseeks.box.controller;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
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
    private final SkillRepository skillRepo;
    private final SecurityConstants securityConstants;

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ProviderController(ProviderRepository providerRepository, UserService userService,
        SkillRepository skillRepo, SecurityConstants securityConstants
    ) {
        this.providerRepository = providerRepository;
        this.userService = userService;
        this.skillRepo = skillRepo;
        this.securityConstants = securityConstants;
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody ProviderEntity getProviderById(@PathVariable("id") final Integer id) throws NotFoundException {
        ProviderEntity provider = providerRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Provider not found");
        });

        return provider;
    }

    @RequestMapping(value = "/get/{id}/skills/all", method = RequestMethod.GET)
    public @ResponseBody Set<SkillEntity> getProviderSkillsById(@PathVariable("id") final Integer id) throws NotFoundException {
        ProviderEntity provider = providerRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Provider not found");
        });

        return provider.getSkills();
    }

    @RequestMapping(value = "/get/{id}/skills/add/{nameSkill}", method = RequestMethod.GET)
    public @ResponseBody Set<SkillEntity> addSkillsToProviderById(@PathVariable("id") final Integer id,
        @PathVariable("nameSkill") final String skillName
    ) throws NotFoundException {
        ProviderEntity provider = providerRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Provider not found");
        });

        SkillEntity skill = skillRepo.findByName(skillName).orElse(new SkillEntity(skillName));

        provider.getSkills().add(skill);

        providerRepository.save(provider);

        return provider.getSkills();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void registerProvider(@RequestBody @Validated(UserEntity.ValidationRegister.class) ProviderEntity provider) {
        LOGGER.info("Provider " + provider.getUsername() + " registers now ...");

        userService.saveUser(provider);
    }
    
    @Secured({"ROLE_PROVIDER"})
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    public void editConsumer(@RequestBody @Validated(UserEntity.ValidationEdit.class)
        ProviderEntity provider, Authentication auth, HttpServletResponse response
    ) {
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

        response.addHeader(
            securityConstants.HEADER_STRING,
            securityConstants.TOKEN_PREFIX + userService.getJWTToken(oldProvider)
        );
    }
}
