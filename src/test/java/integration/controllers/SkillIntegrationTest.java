package integration.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
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

import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    public void isAddingSkillToProviderByName() throws Exception {
        // declarations:
        ProviderEntity provider = makeProvider();
        List<SkillEntity> expected = singletonList(makeSkill(1, "test"));
        // preconditions:
        providerRepository.save(provider);
        // then:
        assertValidResultFrom(() -> post("/skill/insert/test"), provider, jsonAsObjectIsEqualTo(expected));
    }

    @Test
    public void isGettingSkillsByName() throws Exception {
        // declarations:
        final String SEARCH_TERM = "tes";
        final String SEARCH_RESULTS_LIMIT = "10";
        List<SkillEntity> skills = asList(makeSkill(1, "test"),
                makeSkill(2, "testing"),
                makeSkill(3, "none"));
        List<SkillEntity> expected = asList(makeSkill(1, "test"),
                makeSkill(2, "testing"));
        // preconditions:
        skillRepository.save(skills);
        // then:
        assertValidResultFrom(() -> get("/skill/get/" + SEARCH_TERM + "/" + SEARCH_RESULTS_LIMIT),
                makeProvider(), jsonAsObjectIsEqualTo(expected));
    }

    @Test
    public void isGettingSkillById() throws Exception {
        // declarations:
        final String WANTED_ID = "1";
        SkillEntity expected = makeSkill(1, "Test");
        List<SkillEntity> skills = asList(expected, makeSkill(2, ""));
        // preconditions:
        skillRepository.save(skills);
        // then:
        assertValidResultFrom(() -> get("/skill/get/" + WANTED_ID), makeProvider(),
                jsonAsObjectIsEqualTo(expected));
    }

    @Test
    public void isDeletingSkillByIdFromProvider() throws Exception {
        // declarations:
        SkillEntity first = makeSkill(1, "testing");
        SkillEntity next = makeSkill(2, "coding");
        ProviderEntity provider = makeProvider();
        // preconditions:
        providerRepository.save(provider);
        provider.setSkills(asList(first, next));
        providerRepository.save(provider);
        // then:
        assertValidResultFrom(() -> delete("/skill/delete/1"), provider,
                jsonPath("$[0].name", is("coding")));
    }

    private void assertValidResultFrom(final Supplier<MockHttpServletRequestBuilder> method,
                                       final UserEntity user,
                                       final ResultMatcher matcher) throws Exception {
        mockMvc.perform(method.get()
                .principal(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(matcher);
    }

    private <T> ResultMatcher jsonAsObjectIsEqualTo(final T object) throws JsonProcessingException {
        return content().json(mapper.writeValueAsString(object));
    }

    private ProviderEntity makeProvider() {
        ProviderEntity provider = new ProviderEntity();
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(provider, null, provider.getAuthorities()));
        return provider;
    }

    private SkillEntity makeSkill(final Integer id, final String name) {
        return new SkillEntity(id, name);
    }
}
