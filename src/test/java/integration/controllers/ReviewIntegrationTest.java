package integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.*;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.ReviewRepository;
import meeseeks.box.utils.ReviewBuilder;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.function.Supplier;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void isInsertingReviewForProvider() throws Exception {
        // when:
        final String ID_JOB = "1";
        final String ID_PROVIDER = "1";
        ProviderEntity provider = makeProvider("provider", "password", "provider@test.com");
        ConsumerEntity consumer = makeConsumer("consumer", "consumer@test.com");
        JobEntity job = makeJob();
        ReviewEntity review = makeReview("Text", 1);
        String requestBody = mapper.writeValueAsString(review);
        // preconditions:
        providerRepository.save(provider);
        consumerRepository.save(consumer);
        jobRepository.save(job);
        // then:
        assertValidResultFrom(() -> post("/review/insert/" + ID_JOB + "/provider/" + ID_PROVIDER)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody),
                consumer, jsonPath("$.message", is("Text")), status().isOk());
    }

    @Test
    public void isInsertingReviewForConsumer() throws Exception {
        // declarations:
        final String ID_JOB = "1";
        final String ID_CONSUMER = "2";
        ConsumerEntity consumer = makeConsumer("consumer", "consumer@test.com");
        ProviderEntity provider = makeProvider("provider", "password", "provider@test.com");
        JobEntity job = makeJob();
        ReviewEntity review = makeReview("Test", 1);
        String reviewBody = mapper.writeValueAsString(review);
        // preconditions
        providerRepository.save(provider);
        consumerRepository.save(consumer);
        jobRepository.save(job);
        // then:
        assertValidResultFrom(() -> post("/review/insert/" + ID_JOB + "/consumer/" + ID_CONSUMER)
                        .contentType(MediaType.APPLICATION_JSON).content(reviewBody),
                provider, jsonPath("$.message", is("Test")), status().isOk());
    }

    @Test
    public void isUpdatingReviewForConsumer() throws Exception {
        // declarations:
        final String ID_REVIEW = "1";
        ConsumerEntity consumer = makeConsumer("consumer", "consumer@test.com");
        ProviderEntity provider = makeProvider("provider", "password", "provider@test.com");
        JobEntity job = makeJob();
        ReviewEntity review = makeReview("Test", 1);
        // preconditions:
        buildAndSaveReviewBasedOn(provider, consumer, job, review, true);
        // then:
        assertValidResultFrom(() -> post("/review/update/consumer/" + ID_REVIEW)
                        .param("message", "NewTest"),
                provider, jsonPath("$.message", is("NewTest")), status().isAccepted());
    }

    @Test
    public void isUpdatingReviewForProvider() throws Exception {
        // declarations:
        final String ID_REVIEW = "1";
        ProviderEntity provider = makeProvider("provider", "password", "provider@test.com");
        ConsumerEntity consumer = makeConsumer("consumer", "consumer@test.com");
        JobEntity job = makeJob();
        ReviewEntity review = makeReview("Test", 1);
        // preconditions:
        buildAndSaveReviewBasedOn(provider, consumer, job, review, false);
        // then:
        assertValidResultFrom(() -> post("/review/update/provider/" + ID_REVIEW)
                        .param("message", "NewTest"),
                consumer, jsonPath("$.message", is("NewTest")), status().isAccepted());
    }


    @Test
    public void isGettingLatestReviewsFromConsumer() throws Exception {
        // declarations:
        final String ID_CONSUMER = "1";
        final String GET_LIMIT = "10";
        final String RECEIVED = "false";
        ProviderEntity provider = makeProvider("provider", "password", "provider@test.com");
        ConsumerEntity consumer = makeConsumer("consumer", "consumer@test.com");
        JobEntity job = makeJob();
        ReviewEntity review = makeReview("Test", 1);
        // preconditions:
        buildAndSaveReviewBasedOn(provider, consumer, job, review, true);
        // then:
        assertValidResultFrom(() -> get("/review/latest/consumer/" + ID_CONSUMER + "/" + GET_LIMIT + "/" + RECEIVED),
                consumer, jsonPath("$[0].id", is(1)), status().isOk());
    }

    @Test
    public void isGettingLatestReviewsFromProvider() throws Exception {
        // declarations:
        final String ID_PROVIDER = "2";
        final String GET_LIMIT = "10";
        final String RECEIVED = "true";
        ConsumerEntity consumer = makeConsumer("consumer", "consumer@test.com");
        ProviderEntity provider = makeProvider("provider", "password", "provider@test.com");
        JobEntity job = makeJob();
        ReviewEntity review = makeReview("Test", 1);
        // preconditions:
        buildAndSaveReviewBasedOn(provider, consumer, job, review, true);
        reviewRepository.save(review);
        // then:
        assertValidResultFrom(() -> get("/review/latest/provider/" + ID_PROVIDER + "/" + GET_LIMIT + "/" + RECEIVED),
                provider, jsonPath("$[0].id", is(1)), status().isOk());
    }

    private ReviewEntity makeReview(final String message, final Integer rating) {
        return new ReviewEntity(message, rating);
    }

    private void buildAndSaveReviewBasedOn(final ProviderEntity provider,
                                           final ConsumerEntity consumer,
                                           final JobEntity job,
                                           final ReviewEntity review,
                                           final Boolean received) {
        reviewRepository.save(new ReviewBuilder(review)
                .setConsumer(consumerRepository.save(consumer))
                .setProvider(providerRepository.save(provider))
                .setJob(jobRepository.save(job))
                .setRecievedByProvider(received).build());
    }

    private void assertValidResultFrom(final Supplier<MockHttpServletRequestBuilder> method,
                                       final UserEntity user,
                                       final ResultMatcher matcher,
                                       final ResultMatcher status) throws Exception {
        mockMvc.perform(method.get()
                .principal(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())))
                .andDo(print())
                .andExpect(status)
                .andExpect(matcher);
    }

    private ConsumerEntity makeConsumer(final String username, final String email) {
        ConsumerEntity consumer = new ConsumerEntity(email, username);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(consumer,
                        null, consumer.getAuthorities()));
        return consumer;
    }

    private JobEntity makeJob() {
        return new JobEntity();
    }

    private ProviderEntity makeProvider(final String username, final String password, final String email) {
        ProviderEntity provider = new ProviderEntity(username, password, email);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(provider,
                        null, provider.getAuthorities()));
        return provider;
    }
}
