import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.*;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.ReviewRepository;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    private ProviderEntity provider;
    private ConsumerEntity consumer;

    @Before
    public void setUp() {
        provider = providerRepository.save(new ProviderEntity(
                "provider", "test", "test@provider.com"));
        consumer = consumerRepository.save(new ConsumerEntity(
                "consumer", "test", "test@consumer.com"));
    }

    @Test
    public void whenInsertingReviewForProvider_ReviewValid_ExpectReviewInserted() throws Exception {
        // given:
        authenticateUser(consumer);
        JobEntity job = new JobEntity();
        job.setConsumer(consumer);
        job = jobRepository.save(job);
        ReviewEntity review = new ReviewEntity("test", 10);
        // when:
        RequestBuilder request = post("/review/insert/"
                + job.getId() + "/provider/" + provider.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(review));
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> jsonPath("$.message", is("test")));
    }

    @Test
    public void whenInsertingReviewForConsumer_ReviewValid_ExpectReviewInserted() throws Exception {
        // given:
        authenticateUser(provider);
        JobEntity job = new JobEntity();
        job.setConsumer(consumer);
        job = jobRepository.save(job);
        ReviewEntity review = new ReviewEntity("test", 10);
        // when:
        RequestBuilder request = post("/review/insert/"
                + job.getId() + "/consumer/" + consumer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(review));
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> jsonPath("$.message", is("test")));
    }

    @Test
    public void whenUpdatingReviewForConsumer_ReviewExists_ExpectReviewUpdated() throws Exception {
        // given:
        authenticateUser(provider);
        JobEntity job = new JobEntity();
        job.setConsumer(consumer);
        job = jobRepository.save(job);
        ReviewEntity review = new ReviewEntity("test", 10,
                consumer, provider, false);
        reviewRepository.save(review);
        // when:
        RequestBuilder request = post("/review/update/consumer/" + review.getId())
                .param("rating", "10")
                .param("message", "testing");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> jsonPath("$.message", is("testing")));
    }

    @Test
    public void whenUpdatingReviewForProvider_ReviewExists_ExpectReviewUpdated() throws Exception {
        // given:
        authenticateUser(consumer);
        JobEntity job = new JobEntity();
        job.setConsumer(consumer);
        job = jobRepository.save(job);
        ReviewEntity review = new ReviewEntity("test", 10,
                consumer, provider, true);
        reviewRepository.save(review);
        // when:
        RequestBuilder request = post("/review/update/provider/" + review.getId())
                .param("rating", "10")
                .param("message", "testing");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> jsonPath("$.message", is("testing")));
    }

    @Test
    public void whenGettingLatestReviewsForConsumer_ReviewsExist_ExpectReviews() throws Exception {
        // given:
        authenticateUser(consumer);
        JobEntity job = new JobEntity();
        job.setConsumer(consumer);
        job = jobRepository.save(job);
        List<ReviewEntity> reviews = Stream.of(
                new ReviewEntity("test", 10,
                        consumer, provider, false),
                new ReviewEntity("double", 10,
                        consumer, provider, false),
                new ReviewEntity("message", 10,
                        consumer, provider, false))
                .map(it -> reviewRepository.save(it))
                .collect(Collectors.toList());
        // when:
        RequestBuilder request = get("/review/latest/consumer/" +
                consumer.getId()+ "/2/true").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content()
                        .json(mapper.writeValueAsString(reviews.subList(0, 2)))));
    }

    @Test
    public void whenGettingLatestReviewsFromProvider_ProviderExists_ExpectLatestReviews() throws Exception {
        // given:
        authenticateUser(provider);
        JobEntity job = new JobEntity();
        job.setConsumer(consumer);
        job = jobRepository.save(job);
        List<ReviewEntity> reviews = Stream.of(
                new ReviewEntity("test", 10,
                        consumer, provider, true),
                new ReviewEntity("double", 10,
                        consumer, provider, true),
                new ReviewEntity("message", 10,
                        consumer, provider, true))
                .map(it -> reviewRepository.save(it))
                .collect(Collectors.toList());
        // when:
        RequestBuilder request = get("/review/latest/provider/" +
                provider.getId()+ "/2/true").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content()
                        .json(mapper.writeValueAsString(reviews.subList(0, 2)))));
    }

    private void authenticateUser(final UserEntity user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null,
                        user.getAuthorities()));
    }
}
