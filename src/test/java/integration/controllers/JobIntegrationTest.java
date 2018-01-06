package integration.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.*;
import meeseeks.box.model.JobModel;
import meeseeks.box.repository.*;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.jooq.lambda.Unchecked;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Request;
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
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.sql.Time;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private RequestRepository requestRepository;

    @Autowired
    private ObjectMapper mapper;

    private ProviderEntity provider;

    private ConsumerEntity consumer;

    @Before
    public void setUp() {
        consumer = consumerRepository.save(new ConsumerEntity("consumer", "consumer"));
        provider = providerRepository.save(new ProviderEntity("provider", "provider"));
        setTime(consumer.getCreated());
        setTime(provider.getCreated());
    }

    private void authenticateUser(final UserEntity user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    private JobModel makeJobModel(final JobEntity job) {
        AvailabilityEntity availability = new AvailabilityEntity("Monday",
                new Time(new Random().nextInt(10000)),
                new Time(new Random().nextInt(10000)));
        CategoryEntity category = new CategoryEntity(
                Integer.toString(new Random().nextInt(100000)));
        return new JobModel(job, singletonList(availability), category);
    }

    @Test
    public void whenInsertingJob_withValidData_expectJobInserted()
            throws Exception {
        // given:
        authenticateUser(consumerRepository.findOne(1));
        JobModel model = makeJobModel(new JobEntity("Name"));
        // when:
        RequestBuilder request = post("/job/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(model));
        // then:
        assertExpectedResultEquals(request,
                () -> jsonPath("$.name", Is.is("Name")));
    }

    @Test
    public void whenUpdatingJob_andJobExistsInDatabase_expectJobUpdated()
            throws Exception {
        // given:
        authenticateUser(consumer);
        JobEntity job = new JobEntity("Test");
        job.setConsumer(consumerRepository.findOne(1));
        JobEntity jobSavedInDatabase = jobRepository.save(job);
        // when:
        jobSavedInDatabase.setName("Update");
        RequestBuilder request = patch("/job/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(makeJobModel(jobSavedInDatabase)));
        // then:
        assertExpectedResultEquals(request,
                () -> jsonPath("$.name", Is.is("Update")));
    }

    @Test
    public void whenDeletingJob_withValidData_expectJobDeleted()
            throws Exception {
        // given:
        authenticateUser(consumer);
        final JobEntity job = new JobEntity();
        job.setConsumer(consumer);
        final JobEntity jobSavedInRepository = jobRepository.save(job);
        // when:
        RequestBuilder request = delete(
                "/job/delete/" + jobSavedInRepository.getId())
                .contentType(MediaType.APPLICATION_JSON).content("");
        // then:
        assertExpectedStatusEquals(request, () -> status().isAccepted());
        assertThat(jobRepository.findOne(1), IsNull.nullValue());
    }

    private void assertExpectedStatusEquals(
            final RequestBuilder request,
            final Supplier<ResultMatcher> status) throws Exception {
        performRequestAndPrintResult(request)
                .andExpect(status.get());
    }

    private void assertExpectedResultEquals(
            final RequestBuilder request,
            final Supplier<ResultMatcher> matcher) throws Exception {
        performRequestAndPrintResult(request)
                .andExpect(matcher.get());
    }

    private ResultActions performRequestAndPrintResult(RequestBuilder request)
            throws Exception {
        return mockMvc.perform(request).andDo(print());
    }

    public void setTime(Calendar calendar) {
        calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
    }

    @Test
    public void whenGettingLatestJobsCreatedByConsumer_jobsExists_expectJobsFromServer() throws Exception {
        // given:
        authenticateUser(consumer);
        List<JobModel> models = asList(
                makeJobModel(new JobEntity("Test")),
                makeJobModel(new JobEntity("Next")),
                makeJobModel(new JobEntity("Another")));
        List<JobEntity> jobs = models.stream()
                .map(it -> it.build(consumer))
                .map(it -> jobRepository.save(it))
                .sorted(Comparator.comparing(JobEntity::getCreated))
                .collect(Collectors.toList());
        jobs.forEach(it -> setTime(it.getCreated()));
        List<JobEntity> expected = jobs.subList(0, 2);
        // when:
        RequestBuilder request = get("/job/latest/consumer/2").content("");
        // then:
        assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper.writeValueAsString(expected))));
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
        setTime(expected.getCreated());
        final RequestEntity m = new RequestEntity(provider, job, "m");

        JobEntity job1 = new JobEntity("Test1", "Testing1", "TestCity1", "Volunteer1", 100.0);
        AvailabilityEntity availability1 = new AvailabilityEntity("Monday1", new Time(1), new Time(2));
        CategoryEntity category1 = new CategoryEntity("Testing1");
        JobModel model1 = new JobModel(job1, new ArrayList<>(singletonList(availability1)), category1);
        JobEntity expected1 = model1.build(consumer);
        jobRepository.save(expected1);
        setTime(expected1.getCreated());
        final RequestEntity m2 = new RequestEntity(provider, job1, "m");

        List<RequestEntity> requests = asList(m, m2);
        requestRepository.save(requests);
        provider.setRequests(requests);

        JobEntity job2 = new JobEntity("Test2", "Testing2", "TestCity2", "Volunteer2", 100.0);
        AvailabilityEntity availability2 = new AvailabilityEntity("Monday2", new Time(2), new Time(2));
        CategoryEntity category2 = new CategoryEntity("Testing2");
        JobModel model2 = new JobModel(job2, new ArrayList<>(singletonList(availability2)), category2);
        ConsumerEntity consumer2 = consumerRepository.save(new ConsumerEntity("a", "a"));
        setTime(consumer2.getCreated());
        JobEntity expected2 = model2.build(consumer2);
        jobRepository.save(expected2);
        setTime(expected2.getCreated());
        List<JobEntity> expectedList = asList(expected, expected1);

        assertValidResultFrom(() -> get("/job/latest/provider/2"), provider, jsonAsObjectIsEqualTo(expectedList));
        assertValidResultFrom(() -> get("/job/latest/provider/1"), provider, jsonAsObjectIsEqualTo(asList(expected)));
    }

    @Test
    public void testgetLatestJobsByCategory() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(provider, null, provider.getAuthorities()));

        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 100.0);
        AvailabilityEntity availability = new AvailabilityEntity("Monday", new Time(0), new Time(0));
        CategoryEntity category = new CategoryEntity("Testing");
        JobModel model = new JobModel(job, new ArrayList<>(singletonList(availability)), category);
        JobEntity expected = model.build(consumer);
        jobRepository.save(expected);
        setTime(expected.getCreated());

        JobEntity job1 = new JobEntity("Test1", "Testing1", "TestCity1", "Volunteer1", 100.0);
        AvailabilityEntity availability1 = new AvailabilityEntity("Monday1", new Time(1), new Time(2));
        CategoryEntity category1 = new CategoryEntity("Testing1");
        JobModel model1 = new JobModel(job1, new ArrayList<>(singletonList(availability1)), category1);
        JobEntity expected1 = model1.build(consumer);
        jobRepository.save(expected1);
        setTime(expected1.getCreated());

        JobEntity job2 = new JobEntity("Test2", "Testing2", "TestCity2", "Volunteer2", 100.0);
        AvailabilityEntity availability2 = new AvailabilityEntity("Monday2", new Time(2), new Time(2));
        CategoryEntity category2 = new CategoryEntity("Testing2");
        JobModel model2 = new JobModel(job2, new ArrayList<>(singletonList(availability2)), category2);
        ConsumerEntity consumer2 = consumerRepository.save(new ConsumerEntity("a", "a"));
        setTime(consumer2.getCreated());
        JobEntity expected2 = model2.build(consumer2);
        jobRepository.save(expected2);
        setTime(expected2.getCreated());

        List<JobEntity> expectedList = asList(expected);

        assertValidResultFrom(() -> get("/job/find/category/1"), provider, mapper.writeValueAsString(category), jsonAsObjectIsEqualTo(expectedList));
    }

    @Test
    public void whenGettingLatestJobsByLocation_withOneAsPageSize_expectJustOneJobBack() throws Exception {
        // given:
        authenticateUser(provider);
        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 100.0);
        AvailabilityEntity availability = new AvailabilityEntity("Monday", new Time(0), new Time(0));
        CategoryEntity category = new CategoryEntity("Testing");
        JobModel model = new JobModel(job, new ArrayList<>(singletonList(availability)), category);
        JobEntity expected = model.build(consumer);
        jobRepository.save(expected);
        setTime(expected.getCreated());

        JobEntity job1 = new JobEntity("Test1", "Testing1", "TestCity1", "Volunteer1", 100.0);
        AvailabilityEntity availability1 = new AvailabilityEntity("Monday1", new Time(1), new Time(2));
        CategoryEntity category1 = new CategoryEntity("Testing1");
        JobModel model1 = new JobModel(job1, new ArrayList<>(singletonList(availability1)), category1);
        JobEntity expected1 = model1.build(consumer);
        jobRepository.save(expected1);
        setTime(expected1.getCreated());

        JobEntity job2 = new JobEntity("Test2", "Testing2", "TestCity2", "Volunteer2", 100.0);
        AvailabilityEntity availability2 = new AvailabilityEntity("Monday2", new Time(2), new Time(2));
        CategoryEntity category2 = new CategoryEntity("Testing2");
        JobModel model2 = new JobModel(job2, new ArrayList<>(singletonList(availability2)), category2);
        ConsumerEntity consumer2 = consumerRepository.save(new ConsumerEntity("a", "a"));
        setTime(consumer2.getCreated());
        JobEntity expected2 = model2.build(consumer2);
        jobRepository.save(expected2);
        setTime(expected2.getCreated());

        List<JobEntity> expectedList = asList(expected);

        assertValidResultFrom(() -> get("/job/find/location/TestCity/1"), provider, jsonAsObjectIsEqualTo(expectedList));
    }

    @Test
    public void testgetLatestJobsByPriceBetween() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(provider, null, provider.getAuthorities()));

        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 10.0);
        AvailabilityEntity availability = new AvailabilityEntity("Monday", new Time(0), new Time(0));
        CategoryEntity category = new CategoryEntity("Testing");
        JobModel model = new JobModel(job, new ArrayList<>(singletonList(availability)), category);
        JobEntity expected = model.build(consumer);
        jobRepository.save(expected);
        setTime(expected.getCreated());

        JobEntity job1 = new JobEntity("Test1", "Testing1", "TestCity1", "Volunteer1", 30.0);
        AvailabilityEntity availability1 = new AvailabilityEntity("Monday1", new Time(1), new Time(2));
        CategoryEntity category1 = new CategoryEntity("Testing1");
        JobModel model1 = new JobModel(job1, new ArrayList<>(singletonList(availability1)), category1);
        JobEntity expected1 = model1.build(consumer);
        jobRepository.save(expected1);
        setTime(expected1.getCreated());

        JobEntity job2 = new JobEntity("Test2", "Testing2", "TestCity2", "Volunteer2", 100.0);
        AvailabilityEntity availability2 = new AvailabilityEntity("Monday2", new Time(2), new Time(2));
        CategoryEntity category2 = new CategoryEntity("Testing2");
        JobModel model2 = new JobModel(job2, new ArrayList<>(singletonList(availability2)), category2);
        ConsumerEntity consumer2 = consumerRepository.save(new ConsumerEntity("a", "a"));
        setTime(consumer2.getCreated());
        JobEntity expected2 = model2.build(consumer2);
        jobRepository.save(expected2);
        setTime(expected2.getCreated());

        List<JobEntity> expectedList = asList(expected, expected1);

        assertValidResultFrom(() -> get("/job/find/price_between/0/60/2"), provider, jsonAsObjectIsEqualTo(expectedList));
    }

    @Test
    public void testgetLatestJobsByType() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(provider, null, provider.getAuthorities()));

        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 10.0);
        AvailabilityEntity availability = new AvailabilityEntity("Monday", new Time(0), new Time(0));
        CategoryEntity category = new CategoryEntity("Testing");
        JobModel model = new JobModel(job, new ArrayList<>(singletonList(availability)), category);
        JobEntity expected = model.build(consumer);
        jobRepository.save(expected);
        setTime(expected.getCreated());

        JobEntity job1 = new JobEntity("Test1", "Testing1", "TestCity1", "Volunteer1", 30.0);
        AvailabilityEntity availability1 = new AvailabilityEntity("Monday1", new Time(1), new Time(2));
        CategoryEntity category1 = new CategoryEntity("Testing1");
        JobModel model1 = new JobModel(job1, new ArrayList<>(singletonList(availability1)), category1);
        JobEntity expected1 = model1.build(consumer);
        jobRepository.save(expected1);
        setTime(expected1.getCreated());

        JobEntity job2 = new JobEntity("Test2", "Testing2", "TestCity2", "Volunteer2", 100.0);
        AvailabilityEntity availability2 = new AvailabilityEntity("Monday2", new Time(2), new Time(2));
        CategoryEntity category2 = new CategoryEntity("Testing2");
        JobModel model2 = new JobModel(job2, new ArrayList<>(singletonList(availability2)), category2);
        ConsumerEntity consumer2 = consumerRepository.save(new ConsumerEntity("a", "a"));
        setTime(consumer2.getCreated());
        JobEntity expected2 = model2.build(consumer2);
        jobRepository.save(expected2);
        setTime(expected2.getCreated());

        List<JobEntity> expectedList = asList(expected);

        assertValidResultFrom(() -> get("/job/find/type/Volunteer/1"), provider, jsonAsObjectIsEqualTo(expectedList));
    }

    public JobModel makeJob(List<AvailabilityEntity> availabilities, CategoryEntity category) {
        return new JobModel(new JobEntity(), availabilities, category);
    }

    public AvailabilityEntity makeAvailability(final String day, final Time startHour, final Time endHour) {
        return new AvailabilityEntity(day, startHour, endHour);
    }

    public CategoryEntity makeCategory(String name) {
        return new CategoryEntity(name);
    }

    private <T> ResultMatcher jsonAsObjectIsEqualTo(final T object) throws JsonProcessingException {
        return content().json(mapper.writeValueAsString(object));
    }

    private void assertValidResult(final Supplier<MockHttpServletRequestBuilder> method,
                                   final ResultMatcher matcher, UserEntity user) throws Exception {
        performRequestAndPrintResult(method.get()
                .principal(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())))
                .andExpect(matcher);
    }

    private void assertValidResultFrom(final Supplier<MockHttpServletRequestBuilder> method,
                                       final UserEntity user,
                                       final ResultMatcher matcher) throws Exception {
        performRequestAndPrintResult(method.get()
                .principal(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(matcher);
    }

    private void assertValidResultFrom(final Supplier<MockHttpServletRequestBuilder> method,
                                       final UserEntity user,
                                       String content,
                                       final ResultMatcher matcher) throws Exception {
        performRequestAndPrintResult(method.get()
                .principal(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(matcher);
    }
}