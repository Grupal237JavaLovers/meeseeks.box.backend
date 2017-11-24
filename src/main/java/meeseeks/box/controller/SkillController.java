package meeseeks.box.controller;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.List;

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

    @GetMapping("/get/{name}/{limit}")
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    public List<SkillEntity> getSkillsByName(@PathVariable("name") final String name,
                                             @PathVariable("limit") final Integer limit) {
        return skillRepository.findAllByNameContaining(name, new PageRequest(0, limit));
    }

    @ResponseBody
    @GetMapping("/get/{id}")
    @Secured({"ROLE_PROVIDER", "ROLE_CONSUMER"})
    public SkillEntity getSkillById(@PathVariable("id") final Integer id) throws NotFoundException {
        return skillRepository.findById(id).orElseThrow(() -> new NotFoundException("Skill Not Found!"));
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @PostMapping("/insert/{name}")
    public List<SkillEntity> addSkillsToProviderByName(@PathVariable("name") final String name,
                                                      @AuthenticationPrincipal @ApiIgnore ProviderEntity provider) throws NotFoundException {
        List<SkillEntity> skills = skillRepository.getAllSkillsForCurrentProvider();
        skills.add(skillRepository.findByName(name).orElse(new SkillEntity(name)));
        provider.setSkills(skills);
        providerRepository.save(provider);
        return skillRepository.getAllSkillsForCurrentProvider();
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @DeleteMapping("/delete/{id}")
    public List<SkillEntity> deleteSkillFromProviderById(@PathVariable("id") final Integer id,
                                                        @AuthenticationPrincipal @ApiIgnore ProviderEntity provider) throws NotFoundException {
        List<SkillEntity> skills = skillRepository.getAllSkillsForCurrentProvider();
        skills.remove(skillRepository.findById(id).orElseThrow(() -> new NotFoundException("Skill Not Found")));
        provider.setSkills(skills);
        providerRepository.save(provider);
        return skillRepository.getAllSkillsForCurrentProvider();
    }

    @ResponseBody
    @Secured({"ROLE_PROVIDER"})
    @GetMapping("/get/all")
    public List<SkillEntity> getAllSkillsFromProvider() {
        return skillRepository.getAllSkillsForCurrentProvider();
    }
}
