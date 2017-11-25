package integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import org.hamcrest.collection.IsEmptyIterable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tiron Andreea-Ecaterina
 * @version 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MeeseeksBox.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:integrationtests.properties")
public class SkillIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ProviderRepository providerRepository;

    private ProviderEntity provider;

    @Before
    public void setUp() throws Exception {
        provider = new ProviderEntity();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(provider, null, provider.getAuthorities()));
    }

    @Test
    public void isDeletingSkillsById() throws Exception {
        SkillEntity skill = makeSkill(1, "skill");
        SkillEntity skill2= makeSkill(2,"skill2");

        // preconditions:
        skillRepository.save(skill);
        skillRepository.save(skill2);
        providerRepository.save(provider);
        provider.setSkills(asList(skillRepository.findOne(1), skillRepository.findOne(2)));
        providerRepository.save(provider);

        System.out.println(skillRepository.getAllSkillsForCurrentProvider());
        // then:
        assertValidResultFrom(() -> delete("/skill/delete/1"),
                jsonPath("$", IsEmptyIterable.emptyIterable()));
    }

    @Test
    public void isDeletingSkillById() throws Exception {
        SkillEntity skill = makeSkill(1, "skill");
        providerRepository.save(provider);
        // preconditions:
        List<ProviderEntity> providerEntityList = new ArrayList<>();
        providerEntityList.add(provider);
        skill.setProviders(providerEntityList);
        skillRepository.save(skill);

        // then:
        assertValidResultFrom(() -> delete("/skill/delete/1"),
                jsonPath("$", IsEmptyIterable.emptyIterable()));
    }

    private void assertValidResultFrom(final Supplier<MockHttpServletRequestBuilder> method,
                                       final ResultMatcher matcher) throws Exception {
        mockMvc.perform(method.get()
                .principal(new UsernamePasswordAuthenticationToken(provider, null, provider.getAuthorities())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(matcher);
    }

    private SkillEntity makeSkill(final Integer id, final String name) {
        return new SkillEntity(id, name);
    }
}
