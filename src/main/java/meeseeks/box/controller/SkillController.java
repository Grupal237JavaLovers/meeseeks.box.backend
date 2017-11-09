package meeseeks.box.controller;

import meeseeks.box.domain.SkillEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RestController
@RequestMapping("/skill")
public class SkillController {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillController(final SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
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
}
