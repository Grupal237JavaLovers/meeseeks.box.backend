import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.RequestEntity;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MeeseeksBox.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:integrationtests.properties")
public class ConsumerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ObjectMapper mapper;

    private ConsumerEntity consumer;

    @Before
    public void setUp() {
        consumer = consumerRepository.save(
                new ConsumerEntity("consumer", "password"));
        SecurityContextHolder.getContext().setAuthentication(new
                UsernamePasswordAuthenticationToken(consumer, null, consumer
                .getAuthorities()));
    }

    @Test
    public void whenGettingConsumerById_ConsumerExists_ExpectConsumer() throws Exception {
        // when:
        RequestBuilder request = get("/consumer/get/1").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper
                        .writeValueAsString(consumer))));
    }

    @Test
    public void whenUpdatingConsumer_WithValidData_ExpectConsumerUpdated() throws Exception {
        // given:
        ConsumerEntity consumer = new ConsumerEntity("username",
                "password-complex", "password",
                "Test", "test@test.com", "");
        // when:
        RequestBuilder request = patch("/consumer/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(consumer));
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> status().isAccepted());
    }

    @Test
    public void whenAcceptingProviderForJob_JobAndProviderExist_ExpectProviderAccepted() throws Exception {
        // given:
        JobEntity job = new JobEntity();
        job.setConsumer(consumer);
        JobEntity jobSavedInDatabase = jobRepository.save(job);
        ProviderEntity provider = providerRepository.save(new ProviderEntity());
        requestRepository.save(new RequestEntity(provider, jobSavedInDatabase, "message"));
        // when:
        RequestBuilder request = post("/consumer/accept/" +
                jobSavedInDatabase.getId() + "/" + provider.getId()).content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> status().is(202));
    }
}

