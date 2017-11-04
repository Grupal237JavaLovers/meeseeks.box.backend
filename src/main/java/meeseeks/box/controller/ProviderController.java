package meeseeks.box.controller;

import java.util.Set;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javassist.NotFoundException;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import meeseeks.box.service.UserService;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/provider")
public class ProviderController {

    private final ProviderRepository providerRepository;
    private final UserService userService;
    private final SkillRepository skillRepo;

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ProviderController(ProviderRepository providerRepository, UserService userService, SkillRepository skillRepo) {
        this.providerRepository = providerRepository;
        this.userService = userService;
        this.skillRepo = skillRepo;
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody ProviderEntity getProviderById(@PathVariable("id") final Integer id) throws NotFoundException {
        ProviderEntity provider = providerRepository.findOne(id);

        if (provider == null) {
            throw new IllegalArgumentException("Provider not found");
        }

        return provider;
    }

    @RequestMapping(value = "/get/{id}/skills/all", method = RequestMethod.GET)
    public @ResponseBody Set<SkillEntity> getProviderSkillsById(@PathVariable("id") final Integer id) {
        ProviderEntity provider = providerRepository.findOne(id);

        if (provider == null) {
            throw new IllegalArgumentException("Provider not found");
        }

        return provider.getSkills();
    }

    @RequestMapping(value = "/get/{id}/skills/add/{nameSkill}", method = RequestMethod.GET)
    public @ResponseBody Set<SkillEntity> addSkillsToProviderById(@PathVariable("id") final Integer id,
                                                                  @PathVariable("nameSkill") final String skillName) {
        ProviderEntity provider = providerRepository.findOne(id);

        if (provider == null) {
            throw new IllegalArgumentException("Provider not found");
        }

        SkillEntity skill = skillRepo.findByName(skillName).orElse(new SkillEntity(skillName));

        provider.getSkills().add(skill);

        providerRepository.save(provider);

        return provider.getSkills();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void registerProvider(@RequestBody @Valid ProviderEntity provider) {
        LOGGER.info(provider.getUsername() + ": provider tried to register!");

        userService.saveUser(provider);
    }
}
