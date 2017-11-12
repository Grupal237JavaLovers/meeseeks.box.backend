package meeseeks.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.domain.*;
import meeseeks.box.model.JobModel;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.repository.JobRepository;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.UserRepository;
import meeseeks.box.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Time;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by nicof on 11/12/2017.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@ContextConfiguration
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @WithMockUser(username = "test", roles = {"ROLE_PROVIDER"})
    public void deleteUser() throws Exception {
        // when:
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.deleteUser("test")).thenReturn(1);
        Mockito.when(userService.getUserRepository()).thenReturn(userRepository);

        // then:
        mockMvc.perform(get("/user/delete"))
                .andDo(print()).andExpect(status().isAccepted());
    } 
}