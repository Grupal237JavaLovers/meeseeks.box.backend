package integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.*;
import meeseeks.box.model.JobModel;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
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

import java.sql.Time;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
        consumer = consumerRepository.save(
                new ConsumerEntity("provider@test.com", "provider"));
        provider = providerRepository.save(
                new ProviderEntity("consumer", "test"));
    }

    @Test
    public void whenInsertingJob_WithValidData_ExpectJobInserted()
            throws Exception {
        // given:
        authenticateUser(consumerRepository.findOne(1));
        JobModel model = makeJobModel(new JobEntity("Name"));
        // when:
        RequestBuilder request = post("/job/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(model));
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> jsonPath("$.name", Is.is("Name")));
    }

    @Test
    public void whenUpdatingJob_JobExistsInDatabase_ExpectJobUpdated()
            throws Exception {
        // given:
        authenticateUser(consumer);
        JobEntity job = new JobEntity("Test");
        job.setConsumer(consumer);
        JobEntity jobSavedInDatabase = jobRepository.save(job);
        // when:
        jobSavedInDatabase.setName("Update");
        RequestBuilder request = patch("/job/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(makeJobModel(jobSavedInDatabase)));
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> jsonPath("$.name", Is.is("Update")));
    }

    @Test
    public void whenDeletingJob_WithValidData_ExpectJobDeleted()
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
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> status().isAccepted());
        assertThat(jobRepository.findOne(1), IsNull.nullValue());
    }

    @Test
    public void whenGettingLatestJobsCreatedByConsumer_JobsExist_ExpectJobs()
            throws Exception {
        // given:
        authenticateUser(consumer);
        List<JobEntity> jobs = insertJobsIntroRepository();
        List<JobEntity> expected = jobs.subList(0, 2);
        // when:
        RequestBuilder request = get("/job/latest/consumer/2")
                .content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper.writeValueAsString(expected))));
    }

    @Test
    public void whenGettingLatestJobForProvider_JobsExist_ExpectLatestJobs()
            throws Exception {
        // given:
        authenticateUser(provider);
        List<JobEntity> jobs = insertJobsIntroRepository();
        jobs.forEach(it -> requestRepository.save(
                new RequestEntity(provider, it, "message")));
        List<JobEntity> expected = jobs.subList(0, 2);
        // when:
        RequestBuilder request = get("/job/latest/provider/2")
                .content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper.writeValueAsString(expected))));
    }

    @Test
    public void whenGettingLatestJobsByCategory_JobsExist_ExpectLatestJobs()
            throws Exception {
        // given:
        authenticateUser(provider);
        List<JobEntity> jobs = insertJobsIntroRepository();
        List<JobEntity> expected = jobs.subList(0, 1);
        // when:
        RequestBuilder request = get("/job/find/category/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(jobs.get(0).getCategory()));
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper.writeValueAsString(expected))));
    }

    @Test
    public void whenGettingLatestJobsByLocation_JobsExist_ExpectLatestJobs()
            throws Exception {
        // given:
        authenticateUser(provider);
        List<JobEntity> jobs = insertJobsIntroRepository();
        List<JobEntity> expected = jobs.subList(0, 2);
        // when:
        RequestBuilder request = get("/job/find/location/" +
                jobs.get(0).getLocation() + "/2").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper.writeValueAsString(expected))));
    }

    @Test
    public void whenGettingAllCategories_CategoriesExist_ExpectCategories()
            throws Exception {
        // given:
        authenticateUser(provider);
        List<JobEntity> jobs = insertJobsIntroRepository();
        List<CategoryEntity> categories = jobs.stream()
                .map(JobEntity::getCategory)
                .collect(Collectors.toList());
        // when:
        RequestBuilder request = get("/job/categories").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper
                        .writeValueAsString(categories))));
    }

    @Test
    public void whenGettingLatestJobsByPriceBetween_JobsExist_ExpectLatestJobs()
            throws Exception {
        // given:
        authenticateUser(provider);
        List<JobEntity> jobs = insertJobsIntroRepository();
        List<JobEntity> expected = jobs.subList(0, 2);
        final Integer LOWER_LIMIT = 0;
        final Integer UPPER_LIMIT = 2;
        final Integer SIZE = 2;
        // when:
        RequestBuilder request = get("/job/find/price_between/"
                + LOWER_LIMIT + "/" + UPPER_LIMIT + "/" + SIZE).content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper.writeValueAsString(expected))));
    }

    @Test
    public void whenGettingLatestJobsByType_JobsExist_ExpectLatestJobs()
            throws Exception {
        // given:
        authenticateUser(provider);
        List<JobEntity> jobs = insertJobsIntroRepository();
        List<JobEntity> expected = jobs.subList(0, 2);
        // when:
        RequestBuilder request = get("/job/find/type/default/2")
                .content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper.writeValueAsString(expected))));
    }

    private List<JobEntity> insertJobsIntroRepository() {
        return Stream.of(makeJobModel(new JobEntity("Test")),
                makeJobModel(new JobEntity("Next")),
                makeJobModel(new JobEntity("Another")))
                .map(it -> it.build(consumer))
                .map(it -> jobRepository.save(it))
                .sorted(Comparator.comparing(JobEntity::created))
                .collect(Collectors.toList());
    }

    private void authenticateUser(final UserEntity user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null,
                        user.getAuthorities()));
    }

    private JobModel makeJobModel(final JobEntity job) {
        AvailabilityEntity availability = new AvailabilityEntity("Monday",
                new Time(new Random().nextInt(1000000)),
                new Time(new Random().nextInt(1000000)));
        CategoryEntity category = new CategoryEntity(
                Integer.toString(new Random().nextInt(100000)));
        return new JobModel(job, singletonList(availability), category);
    }
}