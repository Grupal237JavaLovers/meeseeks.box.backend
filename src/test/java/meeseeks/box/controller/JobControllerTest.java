package meeseeks.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.domain.AvailabilityEntity;
import meeseeks.box.domain.CategoryEntity;
import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.model.JobModel;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Time;

import static org.mockito.Matchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(JobController.class)
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private ProviderRepository providerRepository;

    @MockBean
    private ConsumerRepository consumerRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void isInsertingJob() throws Exception {
        // declarations:
        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 100.0);
        AvailabilityEntity availability = new AvailabilityEntity("Monday", new Time(0), new Time(0));
        ConsumerEntity consumer = new ConsumerEntity("test", "password", "Name", "test@test.com");
        CategoryEntity category = new CategoryEntity("Testing");
        JobModel model = new JobModel(job, availability, category, consumer);
        JobEntity expected = model.build();
        // when:
        Mockito.when(jobRepository.save((JobEntity) notNull())).thenReturn(expected);
        // then:
        mockMvc.perform(post("/job/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(model)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}