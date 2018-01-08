package integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import org.hamcrest.core.Is;
import org.jooq.lambda.Unchecked;
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
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    public void setUp() {
        provider = providerRepository.save(new ProviderEntity());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(provider,
                        null, provider.getAuthorities()));
    }

    @Test
    public void whenInsertingSkillToProvider_ProviderExists_ExpectSkillInserted()
            throws Exception {
        // given:
        List<SkillEntity> expected =
                singletonList(new SkillEntity(1, "test"));
        // when:
        RequestBuilder request = post("/skill/insert/test").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper
                        .writeValueAsString(expected))));
    }

    @Test
    public void whenGettingSkillByName_SkillExists_ExpectSkill() throws Exception {
        // given:
        final String SEARCH_TERM = "tes";
        final String SEARCH_RESULTS_LIMIT = "10";
        List<SkillEntity> skills = asList(
                new SkillEntity(1, "Test"),
                new SkillEntity(2, "testing"),
                new SkillEntity(3, "none"));
        List<SkillEntity> expected = skills.subList(0, 2);
        skillRepository.save(skills);
        // when:
        RequestBuilder request = get("/skill/get/" +
                SEARCH_TERM + "/" + SEARCH_RESULTS_LIMIT).content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper
                        .writeValueAsString(expected))));
    }

    @Test
    public void whenGettingSkillById_SkillExists_ExpectSkill() throws Exception {
        // declarations:
        SkillEntity expected = new SkillEntity(1, "Test");
        List<SkillEntity> skills = asList(expected,
                new SkillEntity(2, "Another"));
        skillRepository.save(skills);
        // when:
        RequestBuilder request = get("/skill/get/1").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content()
                        .json(mapper.writeValueAsString(expected))));
    }

    @Test
    public void whenDeletingSkillFromProvider_SkillExists_ExpectListOfLeftSkills()
            throws Exception {
        // given:
        provider.setSkills(asList(
                new SkillEntity(1, "First"),
                new SkillEntity(2, "Next")));
        providerRepository.save(provider);
        // when:
        RequestBuilder request = delete("/skill/delete/1").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> jsonPath("$[0].name", Is.is("Next")));
    }
}
