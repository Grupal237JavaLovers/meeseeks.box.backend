package meeseeks.box.controller;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
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
    private final SkillRepository skillRepository;
    private final SecurityConstants securityConstants;

    private final Logger LOGGER =
            Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ProviderController(final ProviderRepository providerRepository,
                              final UserService userService,
                              final SkillRepository skillRepository,
                              final SecurityConstants securityConstants) {
        this.providerRepository = providerRepository;
        this.userService = userService;
        this.skillRepository = skillRepository;
        this.securityConstants = securityConstants;
    }

    @ResponseBody
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ProviderEntity getProviderById(@PathVariable("id") final Integer id) throws NotFoundException {
        return providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider Not Found!"));
    }


    @ResponseBody
    @RequestMapping(value = "/get/{id}/skills", method = RequestMethod.GET)
    public Set<SkillEntity> getProviderSkillsById(@PathVariable("id") final Integer id) throws NotFoundException {
        return providerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Provider Not Found!")).getSkills();
    }

    @ResponseBody
    @RequestMapping(value = "/get/{id}/skills/add/{name}", method = RequestMethod.GET)
    public Set<SkillEntity> addSkillsToProviderByName(@PathVariable("id") final Integer id,
                                                      @PathVariable("name") final String name) throws NotFoundException {
        ProviderEntity provider = providerRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Provider not found"));
        SkillEntity skill = skillRepository.findByName(name).orElse(new SkillEntity(name));
        provider.getSkills().add(skill);
        return providerRepository.save(provider).getSkills();
    }

    @ResponseBody
    @RequestMapping(value = "/get/{idUser}/skills/delete/{idSkill}", method = RequestMethod.GET)
    public Set<SkillEntity> deleteSkillFromProviderById(@PathVariable("idUser") final Integer idUser,
                                                        @PathVariable("idSkill") final Integer idSkill) throws NotFoundException {
        ProviderEntity provider = providerRepository.findById(idUser)
                .orElseThrow(() -> new NotFoundException("Provider Not Found!"));
        SkillEntity skill = skillRepository.findById(idSkill)
                .orElseThrow(() -> new NotFoundException("Skill Not Found!"));
        provider.getSkills().remove(skill);
        return providerRepository.save(provider).getSkills();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void registerProvider(@RequestBody @Validated(UserEntity.ValidationRegister.class) ProviderEntity provider) {
        LOGGER.info("Provider " + provider.getUsername() + " registers now ...");
        userService.saveUser(provider);
    }

    @Secured({"ROLE_PROVIDER"})
    @RequestMapping(value = "/update", method = RequestMethod.PATCH)
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
