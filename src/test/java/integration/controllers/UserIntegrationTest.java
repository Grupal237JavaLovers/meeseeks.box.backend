package integration.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Calendar;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class UserIntegrationTest {

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
    public void setUp() throws Exception {
        provider = new ProviderEntity("provider", "provider");
        consumer = new ConsumerEntity("consumer", "consumer","consumer", "consumer");
        consumer = consumerRepository.save(consumer);
        provider = providerRepository.save(provider);
        //setTime(provider.getCreated());
    }

    @Test
    public void testCurrent() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(consumer,
                null, consumer.getAuthorities()));

        assertValidResultFrom(() -> get("/user/current"), consumer,
                content().json(mapper.writeValueAsString(consumer)));
    }

//    @Test
//    public void testChangePassword() throws Exception {
//        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(consumer,
//                null, consumer.getAuthorities()));
//        ChangePasswordModel model = new ChangePasswordModel("consumer", "password", "password");
//
//        assertValidResultFrom(() -> post("/user/change-password"), consumer,
//                mapper.writeValueAsString(model), status().isAccepted());
//    }

    @Test
    public void testDelete() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(consumer,
                null, consumer.getAuthorities()));

        assertValidResult(() -> delete("/user/delete"), status().isAccepted(), consumer);
    }

    public void setTime(Calendar c){
        c.setTimeInMillis((c.getTimeInMillis()/1000)*1000);
    }

    @Test
    public void testFind() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(consumer,
                null, consumer.getAuthorities()));

        ConsumerEntity entity = new ConsumerEntity("dac", "dac");
        consumerRepository.save(entity);
       // setTime(entity.getCreated());
        ConsumerEntity entity1 = new ConsumerEntity("dons", "dons");
        consumerRepository.save(entity1);
       // setTime(entity1.getCreated());
        List<UserEntity> expectedList = asList(provider);


        mockMvc.perform(get("/user/find/1")
                .principal(new UsernamePasswordAuthenticationToken(consumer, null, consumer.getAuthorities()))
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", "d")
                .param("password", "d"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonAsObjectIsEqualTo(expectedList));
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

    private void assertValidResultFrom(final Supplier<MockHttpServletRequestBuilder> method,
                                       final UserEntity user,
                                       final ResultMatcher matcher) throws Exception {
        mockMvc.perform(method.get()
                .principal(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(matcher);
    }

    private void assertValidResultFrom(final Supplier<MockHttpServletRequestBuilder> method,
                                       final UserEntity user,
                                       String content,
                                       final ResultMatcher matcher) throws Exception {
        mockMvc.perform(method.get()
                .principal(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(matcher);
    }
}
