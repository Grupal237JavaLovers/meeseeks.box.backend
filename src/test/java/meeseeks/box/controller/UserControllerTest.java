package meeseeks.box.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import meeseeks.box.domain.UserEntity;
import meeseeks.box.repository.UserRepository;
import meeseeks.box.service.UserService;

// Test imports
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepo;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private Authentication auth;
    
    @Test
    public void greetingShouldReturnMessageFromService() throws Exception {
        Mockito.when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.ofNullable(null));
        //Mockito.verify(userService).saveUser(Matchers.any(UserEntity.class));
        
        this.mockMvc.perform(get("/user")).andDo(print()).andExpect(status().isOk())
            .andExpect(content().string(containsString("email: test@email.com")));
    }
}