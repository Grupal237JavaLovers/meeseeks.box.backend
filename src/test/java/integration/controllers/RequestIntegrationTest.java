import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.RequestEntity;
import meeseeks.box.model.DateRange;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
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

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MeeseeksBox.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:integrationtests.properties")
public class RequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private JobRepository jobRepository;

    private ProviderEntity provider;

    @Before
    public void setUp() {
        provider = providerRepository.save(new ProviderEntity());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(provider,
                        null, provider.getAuthorities()));
    }

    @Test
    public void whenDeletingRequest_RequestExists_ExpectRequestDeleted()
            throws Exception {
        // given:
        RequestEntity request = new RequestEntity("test", true);
        request.setProvider(provider);
        requestRepository.save(request);
        // when:
        RequestBuilder requestPath =
                delete("/request/delete/1").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(
                requestPath, () -> status().isOk());
    }

    @Test
    public void whenUpdatingRequest_RequestExists_ExpectRequestUpdated() throws Exception {
        // given:
        RequestEntity expected = new RequestEntity("test", true);
        expected.setProvider(provider);
        requestRepository.save(expected);
        // when:
        RequestBuilder requestPath =
                patch("/request/update/1/NewText").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(requestPath,
                () -> jsonPath("$.message", Is.is("NewText")));
    }

    @Test
    public void whenGettingRequestById_RequestExists_ExpectRequestWithThatId() throws Exception {
        // given:
        RequestEntity request = new RequestEntity("test", true);
        request.setProvider(provider);
        requestRepository.save(request);
        // when:
        RequestBuilder requestPath =
                get("/request/get/1").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(requestPath,
                Unchecked.supplier(() -> content().json(mapper.writeValueAsString
                        (requestRepository.findOne(1)))));
    }

    @Test
    public void whenGettingLatestAcceptedRequestsForProvider_ExpectLatestAcceptedRequests() throws Exception {
        // given
        JobEntity job = jobRepository.save(new JobEntity());
        List<RequestEntity> requests = Stream.of(
                new RequestEntity(provider, job, "test"),
                new RequestEntity(provider, job, "message"),
                new RequestEntity(provider, job, "testing"),
                new RequestEntity(provider, job, "other"))
                .peek(it -> it.setAccepted(true))
                .map(it -> requestRepository.save(it))
                .collect(Collectors.toList());
        // when:
        RequestBuilder request = get("/request/latest/provider/accepted/" +
                provider.getId() + "/3").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(
                        mapper.writeValueAsString(requests.subList(0, 3)))));
    }

    @Test
    public void whenGettingLatestRequestsForCurrentProvider_ExpectLatestRequests() throws Exception {
        // given
        JobEntity job = jobRepository.save(new JobEntity());
        List<RequestEntity> requests = Stream.of(
                new RequestEntity(provider, job, "test"),
                new RequestEntity(provider, job, "message"),
                new RequestEntity(provider, job, "testing"),
                new RequestEntity(provider, job, "other"))
                .peek(it -> it.setAccepted(true))
                .map(it -> requestRepository.save(it))
                .collect(Collectors.toList());
        // when:
        RequestBuilder request = get("/request/get/all/2");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(
                        mapper.writeValueAsString(requests.subList(0, 2)))));
    }
}