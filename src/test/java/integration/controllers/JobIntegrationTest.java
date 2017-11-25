package integration.controllers;

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
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private ObjectMapper mapper;

    private ProviderEntity provider;

    private ConsumerEntity consumer;

    @Before
    public void setUp() throws Exception {
        provider = new ProviderEntity();
        consumer = new ConsumerEntity();
    }

    @Test
    public void isInseringJob(){
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(consumer, null, consumer.getAuthorities()));
        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 100.0);
        AvailabilityEntity availability = new AvailabilityEntity("Monday", new Time(0), new Time(0));
        ConsumerEntity consumer = new ConsumerEntity("test", "password", "Name", "test@test.com");
        CategoryEntity category = new CategoryEntity("Testing");
        JobModel model = new JobModel(job, new ArrayList<>(singletonList(availability)), category);
        JobEntity expected = model.build(consumer);

        // then:
        try {
            mockMvc.perform(post("/job/insert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(model)))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(expected)));
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

    public JobModel makeJob(List<AvailabilityEntity> availabilities,CategoryEntity category){
        return new JobModel(new JobEntity(), availabilities, category);
    }

    public AvailabilityEntity makeAvailability(final String day, final Time startHour, final Time endHour){
        return new AvailabilityEntity(day, startHour, endHour);
    }
    public CategoryEntity makeCategory(String name){
        return new CategoryEntity(name);
    }
    private void assertValidResult(final Supplier<MockHttpServletRequestBuilder> method,
                                       final ResultMatcher matcher, UserEntity user) throws Exception {
        mockMvc.perform(method.get()
                .principal(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())))
                .andDo(print())
                .andExpect(matcher);
    }
}
