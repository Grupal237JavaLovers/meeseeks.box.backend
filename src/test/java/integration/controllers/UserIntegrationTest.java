package integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.domain.UserEntity;
import meeseeks.box.model.ChangePasswordModel;
import meeseeks.box.repository.ConsumerRepository;
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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ConsumerRepository consumerRepository;

    @Autowired
    private ObjectMapper mapper;


    private ConsumerEntity consumer;

    @Before
    public void setUp() {
        consumer = consumerRepository.save(
                new ConsumerEntity("consumer", "consumer", "consumer", "consumer"));
    }

    private void authenticateUser(final UserEntity user) {
        SecurityContextHolder.getContext().setAuthentication(new
                UsernamePasswordAuthenticationToken(user, null, user
                .getAuthorities()));
    }

    @Test
    public void whenGettingCurrentUser_UserExists_ExpectCurrentUser() throws Exception {
        // given:
        authenticateUser(consumer);
        // when:
        RequestBuilder request = get("/user/current").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper
                        .writeValueAsString(consumer))));
    }

    @Test
    public void whenChangingPassword_ValidNewPassword_ExpectPasswordChanged() throws Exception {
        // given:
        authenticateUser(consumer);
        ChangePasswordModel model = new ChangePasswordModel("consumer",
                "password", "password");
        // when:
        RequestBuilder request = patch("/user/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(model));
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> status().isAccepted());
    }

    @Test
    public void whenDeletingUser_UserExists_ExpectUserDeleted() throws Exception {
        // given:
        authenticateUser(consumer);
        // when:
        RequestBuilder request = delete("/user/delete").content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                () -> status().isAccepted());
    }

    @Test
    public void whenFindingUserByName_UserExists_ExpectUser() throws Exception {
        // given:
        authenticateUser(consumer);
        consumerRepository.save(asList(
                new ConsumerEntity("testing", "test"),
                new ConsumerEntity("testing@com", "testable")));
        List<UserEntity> expected = singletonList(consumer);
        // when:
        RequestBuilder request = get("/user/find/1")
                .param("name", "consumer")
                .content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper
                        .writeValueAsString(expected))));
    }

    @Test
    public void whenFindingUsersByEmail_UsersExists_ExpectUsers() throws
            Exception {
        // given:
        authenticateUser(consumer);
        ConsumerEntity next = new ConsumerEntity("consumer@com", "test");
        consumerRepository.save(asList(next,
                new ConsumerEntity("testing@com", "testable")));
        List<UserEntity> expected = asList(consumer, next);
        // when:
        RequestBuilder request = get("/user/find/2")
                .param("email", "consumer")
                .content("");
        // then:
        new AssertRequest(mockMvc).assertExpectedResultEquals(request,
                Unchecked.supplier(() -> content().json(mapper
                        .writeValueAsString(expected))));
    }
}
