package meeseeks.box.controller;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private final Logger LOGGER = Logger.getLogger(ProviderController.class.getName());

    @Autowired
    public ProviderController(final ProviderRepository providerRepository, UserService userService) {
        this.providerRepository = providerRepository;
        this.userService = userService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public void create() {
        providerRepository.save(new ProviderEntity("test", "password", "name", "email2"));
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody ProviderEntity getProviderById(@PathVariable("id") final Integer id) {
        return providerRepository.findOne(id);
    }

    @RequestMapping(value = "/get/{id}/skills/all", method = RequestMethod.GET)
    public @ResponseBody Set<SkillEntity> getProviderSkillsById(@PathVariable("id") final Integer id) {
        return providerRepository.findOne(id).getSkills();
    }

    @RequestMapping(value = "/get/{id}/skills/add/{nameSkill}", method = RequestMethod.GET)
    public @ResponseBody Set<SkillEntity> addSkillsToProviderById(@PathVariable("id") final Integer id,
                                                                  @PathVariable("nameSkill") final String nameSkill) {
        ProviderEntity provider = providerRepository.findOne(id);
        Set<SkillEntity> skills = provider.getSkills();
        skills.add(new SkillEntity(nameSkill));
        provider.setSkills(skills);
        providerRepository.save(provider);
        return provider.getSkills();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void registerProvider(@RequestBody @Valid ProviderEntity provider) {
        LOGGER.log(Level.INFO, "Provider {0} try to register!", provider.getUsername());
        userService.saveUser(provider);
    }
}
