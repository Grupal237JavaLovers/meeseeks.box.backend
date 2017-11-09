package meeseeks.box.controller;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RestController
@RequestMapping("/skill")
public class SkillController {

    private final SkillRepository skillRepository;
    private final ProviderRepository providerRepository;

    @Autowired
    public SkillController(final SkillRepository skillRepository,
                           final ProviderRepository providerRepository) {
        this.skillRepository = skillRepository;
        this.providerRepository = providerRepository;
    }

    /**
     * Endpoint used for skill searching.
     * The provider will be able to see a live list of skills from the system as he's typing.
     *
     * @param name  - the skill approximately name
     * @param limit - the limit of the search results
     * @return - list of skills where skill's name contains @name.
     */
    @ResponseBody
    @RequestMapping("/get/{name}/{limit}")
    public List<SkillEntity> getSkillsByName(@PathVariable("name") final String name,
                                             @PathVariable("limit") final Integer limit) {
        return skillRepository.findAllByNameContaining(name, new PageRequest(0, limit));
    }

    @ResponseBody
    @RequestMapping("/get/{id}")
    public SkillEntity getSkillById(@PathVariable("id") final Integer id) throws NotFoundException {
        return skillRepository.findById(id).orElseThrow(() -> new NotFoundException("Skill Not Found!"));
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @RequestMapping(value = "/insert/{name}", method = RequestMethod.GET)
    public Set<SkillEntity> addSkillsToProviderByName(@PathVariable("name") final String name) throws NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ProviderEntity provider = (ProviderEntity) authentication.getPrincipal();
        SkillEntity skill = skillRepository.findByName(name).orElse(new SkillEntity(name));
        provider.getSkills().add(skill);
        return providerRepository.save(provider).getSkills();
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public Set<SkillEntity> deleteSkillFromProviderById(@PathVariable("id") final Integer id) throws NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ProviderEntity provider = (ProviderEntity) authentication.getPrincipal();
        SkillEntity skill = skillRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Skill Not Found!"));
        if (skill.getProviders().contains(provider)) {
            provider.getSkills().remove(skill);
        }
        return providerRepository.save(provider).getSkills();
    }


}
