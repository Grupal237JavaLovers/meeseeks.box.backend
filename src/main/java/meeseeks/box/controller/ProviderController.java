package meeseeks.box.controller;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/provider")
public class ProviderController {

    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderController(final ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public void create() {
        providerRepository.save(new ProviderEntity("test", "password", "name", "email"));
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody ProviderEntity getProviderById(@PathVariable("id") Integer id) {
        return providerRepository.findOne(id);
    }

    @RequestMapping(value = "/get/{id}/skills/all", method = RequestMethod.GET)
    public @ResponseBody Set<SkillEntity> getProviderSkillsById(@PathVariable("id") Integer id) {
        return providerRepository.findOne(id).getSkills();
    }

    @RequestMapping(value = "/get/{id}/skills/add/{nameSkill}", method = RequestMethod.GET)
    public @ResponseBody Set<SkillEntity> addSkillsToProviderById(@PathVariable("id") Integer id, @PathVariable("nameSkill") String nameSkill) {
        ProviderEntity provider = providerRepository.findOne(id);
        Set<SkillEntity> skills = provider.getSkills();
        skills.add(new SkillEntity(nameSkill));
        provider.setSkills(skills);
        providerRepository.save(provider);
        return provider.getSkills();
    }




}
