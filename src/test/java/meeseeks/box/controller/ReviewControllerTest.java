package meeseeks.box.controller;

import static org.mockito.Matchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.JobEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.ReviewEntity;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.ReviewRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private ProviderRepository providerRepository;

    @MockBean
    private ConsumerRepository consumerRepository;

    @MockBean
    private JobRepository jobRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @WithMockUser(username = "consumer", roles = {"CONSUMER"})
    public void testInsertForProvider() throws Exception {
        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 100.0);
        ProviderEntity provider = new ProviderEntity("provider", "test", "name", "email");
        ConsumerEntity consumer = new ConsumerEntity("consumer", "test", "name", "email");

        Authentication authentication = new UsernamePasswordAuthenticationToken(consumer, AuthorityUtils.createAuthorityList("ROLE_CONSUMER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(jobRepository.findById(1)).thenReturn(Optional.of(job));
        Mockito.when(providerRepository.findById(2)).thenReturn(Optional.of(provider));
        Mockito.when(consumerRepository.findOne(Matchers.anyInt())).thenReturn(consumer);

        ReviewEntity review = new ReviewEntity("message", 10);
        ReviewEntity expected = new ReviewEntity("message", 10);
        expected.setProvider(provider);
        expected.setConsumer(consumer);
        expected.setReceivedByProvider(true);
        expected.setJob(job);

        // when:
        Mockito.when(reviewRepository.save((ReviewEntity) notNull())).thenReturn(expected);

        mockMvc.perform(post("/review/insert/1/provider/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(review)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @Test
    @WithMockUser(username = "provider", roles = {"PROVIDER"})
    public void testInsertForConsumer() throws Exception {
        JobEntity job = new JobEntity("Test", "Testing", "TestCity", "Volunteer", 100.0);
        ProviderEntity provider = new ProviderEntity("provider", "test", "name", "email");
        ConsumerEntity consumer = new ConsumerEntity("consumer", "test", "name", "email");

        Authentication authentication = new UsernamePasswordAuthenticationToken(provider, AuthorityUtils.createAuthorityList("ROLE_PROVIDER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(jobRepository.findById(1)).thenReturn(Optional.of(job));
        Mockito.when(consumerRepository.findById(2)).thenReturn(Optional.of(consumer));
        Mockito.when(providerRepository.findOne(Matchers.anyInt())).thenReturn(provider);

        ReviewEntity review = new ReviewEntity("message", 10);
        ReviewEntity expected = new ReviewEntity("message", 10);
        expected.setProvider(provider);
        expected.setConsumer(consumer);
        expected.setReceivedByProvider(false);
        expected.setJob(job);

        // when:
        Mockito.when(reviewRepository.save((ReviewEntity) notNull())).thenReturn(expected);

        mockMvc.perform(post("/review/insert/1/consumer/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(review)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}