package integration.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.*;
import meeseeks.box.model.JobModel;
import meeseeks.box.repository.*;
import org.apache.catalina.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MeeseeksBox.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:integrationtests.properties")
public class JobIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private  RequestRepository requestRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private ObjectMapper mapper;

    private ProviderEntity provider;

    private ConsumerEntity consumer;

    @Before
    public void setUp() throws Exception {
        provider = new ProviderEntity("provider", "provider");
        consumer = new ConsumerEntity("consumer", "consumer");
        consumer = consumerRepository.save(consumer);
        provider = providerRepository.save(provider);
        consumer.getCreated().setTimeInMillis((consumer.getCreated().getTimeInMillis() / 1000) * 1000);
        setTime(provider.getCreated());
    }

    @Test
    public void isInseringJob(){
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(consumer, null, consumer.getAuthorities()));
        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 100.0);
        AvailabilityEntity availability = new AvailabilityEntity("Monday", new Time(0), new Time(0));
        CategoryEntity category = new CategoryEntity("Testing");
        JobModel model = new JobModel(job, new ArrayList<>(singletonList(availability)), category);

        try {
            mockMvc.perform(post("/job/insert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(model)))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(jobRepository.findOne(1))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isDeletingJobById() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(consumer, null, consumer.getAuthorities()));
        JobEntity job = new JobEntity();
        job.setConsumer(consumer);

        // preconditions:
        jobRepository.save(job);
        // then:
        assertValidResult(() -> delete("/job/delete/1"), status().isAccepted(), consumer);
    }

    public void setTime(Calendar c){
        c.setTimeInMillis((c.getTimeInMillis()/1000)*1000);
    }

    @Test
    public void isGettingLatestJobsCreatedByConsumer() throws Exception {
        // declarations:
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(consumer, null, consumer.getAuthorities()));
        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 100.0);
        AvailabilityEntity availability = new AvailabilityEntity("Monday", new Time(0), new Time(0));
        CategoryEntity category = new CategoryEntity("Testing");
        JobModel model = new JobModel(job, new ArrayList<>(singletonList(availability)), category);
        JobEntity expected = model.build(consumer);
        jobRepository.save(expected);
        expected.getCreated().setTimeInMillis((expected.getCreated().getTimeInMillis() / 1000) * 1000);

        JobEntity job1 = new JobEntity("Test1", "Testing1", "TestCity1", "Volunteer1", 100.0);
        AvailabilityEntity availability1 = new AvailabilityEntity("Monday1", new Time(1), new Time(2));
        CategoryEntity category1 = new CategoryEntity("Testing1");
        JobModel model1 = new JobModel(job1, new ArrayList<>(singletonList(availability1)), category1);
        JobEntity expected1 = model1.build(consumer);
        jobRepository.save(expected1);
        expected1.getCreated().setTimeInMillis((expected1.getCreated().getTimeInMillis() / 1000) * 1000);

        JobEntity job2 = new JobEntity("Test2", "Testing2", "TestCity2", "Volunteer2", 100.0);
        AvailabilityEntity availability2 = new AvailabilityEntity("Monday2", new Time(2), new Time(2));
        CategoryEntity category2 = new CategoryEntity("Testing2");
        JobModel model2 = new JobModel(job2, new ArrayList<>(singletonList(availability2)), category2);
        ConsumerEntity consumer2 = consumerRepository.save(new ConsumerEntity("a", "a"));
        consumer2.getCreated().setTimeInMillis((consumer2.getCreated().getTimeInMillis() / 1000) * 1000);
        JobEntity expected2 = model2.build(consumer2);
        jobRepository.save(expected2);
        expected2.getCreated().setTimeInMillis((expected2.getCreated().getTimeInMillis() / 1000) * 1000);

        List<JobEntity> jobs = asList(expected, expected1, expected2);
        List<JobEntity> expectedList = asList(expected, expected1);
        // preconditions:
        // then:
        assertValidResultFrom(() -> get("/job/latest/consumer/2"), consumer, jsonAsObjectIsEqualTo(expectedList));
    }

    @Test
    public void isGettingLatestJobsRequestedbyProvider() throws Exception {
        // declarations:
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(provider, null, provider.getAuthorities()));

        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 100.0);
        AvailabilityEntity availability = new AvailabilityEntity("Monday", new Time(0), new Time(0));
        CategoryEntity category = new CategoryEntity("Testing");
        JobModel model = new JobModel(job, new ArrayList<>(singletonList(availability)), category);
        JobEntity expected = model.build(consumer);
        jobRepository.save(expected);
        expected.getCreated().setTimeInMillis((expected.getCreated().getTimeInMillis() / 1000) * 1000);
        final RequestEntity m = new RequestEntity( provider, job, "m");

        JobEntity job1 = new JobEntity("Test1", "Testing1", "TestCity1", "Volunteer1", 100.0);
        AvailabilityEntity availability1 = new AvailabilityEntity("Monday1", new Time(1), new Time(2));
        CategoryEntity category1 = new CategoryEntity("Testing1");
        JobModel model1 = new JobModel(job1, new ArrayList<>(singletonList(availability1)), category1);
        JobEntity expected1 = model1.build(consumer);
        jobRepository.save(expected1);
        expected1.getCreated().setTimeInMillis((expected1.getCreated().getTimeInMillis() / 1000) * 1000);
        final RequestEntity m2 = new RequestEntity( provider, job1, "m");

        List<RequestEntity> requests = asList(m, m2);
        requestRepository.save(requests);
        provider.setRequests(requests);

        JobEntity job2 = new JobEntity("Test2", "Testing2", "TestCity2", "Volunteer2", 100.0);
        AvailabilityEntity availability2 = new AvailabilityEntity("Monday2", new Time(2), new Time(2));
        CategoryEntity category2 = new CategoryEntity("Testing2");
        JobModel model2 = new JobModel(job2, new ArrayList<>(singletonList(availability2)), category2);
        ConsumerEntity consumer2 = consumerRepository.save(new ConsumerEntity("a", "a"));
        consumer2.getCreated().setTimeInMillis((consumer2.getCreated().getTimeInMillis() / 1000) * 1000);
        JobEntity expected2 = model2.build(consumer2);
        jobRepository.save(expected2);
        expected2.getCreated().setTimeInMillis((expected2.getCreated().getTimeInMillis() / 1000) * 1000);

        List<JobEntity> jobs = asList(expected, expected1, expected2);
        List<JobEntity> expectedList = asList(expected, expected1);

        // then:
        assertValidResultFrom(() -> get("/job/latest/provider/2"), provider, jsonAsObjectIsEqualTo(expectedList));
    }

    public JobModel makeJob(List<AvailabilityEntity> availabilities,CategoryEntity category){
        return new JobModel(new JobEntity(), availabilities, category);
    }

    public AvailabilityEntity makeAvailability(final String day, final Time startHour, final Time endHour){
        return new AvailabilityEntity(day, startHour, endHour);
    }
    public CategoryEntity makeCategory(String name){
        return new CategoryEntity(name);
    }

    private <T> ResultMatcher jsonAsObjectIsEqualTo(final T object) throws JsonProcessingException {
        return content().json(mapper.writeValueAsString(object));
    }

    private void assertValidResult(final Supplier<MockHttpServletRequestBuilder> method,
                                       final ResultMatcher matcher, UserEntity user) throws Exception {
        mockMvc.perform(method.get()
                .principal(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())))
                .andDo(print())
                .andExpect(matcher);
    }

    private RequestEntity makeRequest(final String message, final Boolean accepted) {
        return new RequestEntity(message, accepted);
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
}
