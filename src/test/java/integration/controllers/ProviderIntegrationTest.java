package integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MeeseeksBox.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:integrationtests.properties")
public class ProviderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ObjectMapper mapper;

    private ProviderEntity provider;

    @Before
    public void setUp() {
        provider = providerRepository.save(
                new ProviderEntity("provider", "password"));
        SecurityContextHolder.getContext().setAuthentication(new
                UsernamePasswordAuthenticationToken(provider, null, provider
                .getAuthorities()));
    }

    @Test
    public void whenGettingProviderById_ProviderExists_ExpectProvider() throws Exception {
        // when:
        RequestBuilder request = get("/provider/get/1").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper
                        .writeValueAsString(provider))));
    }

    @Test
    public void whenGettingProviderSkillsById_SkillsExists_ExpectSkill() throws Exception {
        // given:
        List<SkillEntity> skills = Stream.of(
                new SkillEntity("test"),
                new SkillEntity("another"))
                .map(it -> skillRepository.save(it))
                .collect(Collectors.toList());
        provider.setSkills(skills);
        ProviderEntity providerSavedInDatabase = providerRepository.save(provider);
        // when:
        RequestBuilder request = get("/provider/get/1/skills").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper
                        .writeValueAsString(providerSavedInDatabase.getSkills()))));
    }

    @Test
    public void whenUpdatingProvider_WithValidData_ExpectProviderUpdated() throws Exception {
        // given:
        ProviderEntity provider = new ProviderEntity("provider", "test");
        // when:
        RequestBuilder request = patch("/provider/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(provider));
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> status().isAccepted());
    }

    @Test
    public void whenApplyingAsProviderForJob_JobExists_ExpectRequest() throws Exception {
        // given:
        JobEntity job = new JobEntity();
        ConsumerEntity consumer = consumerRepository
                .save(new ConsumerEntity("consumer", "test"));
        job.setConsumer(consumer);
        JobEntity jobSavedInDatabase = jobRepository.save(job);
        // when:
        RequestBuilder request = post("/provider/apply/" +
                jobSavedInDatabase.getId() + "/message").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> jsonPath("$.message", Is.is("message")));
    }
}

