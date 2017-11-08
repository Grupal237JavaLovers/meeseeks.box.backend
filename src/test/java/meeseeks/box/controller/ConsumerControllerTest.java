package meeseeks.box.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import meeseeks.box.domain.ConsumerEntity;
import meeseeks.box.repository.ConsumerRepository;
import meeseeks.box.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(ConsumerController.class)
public class ConsumerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumerRepository consumerRepo;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private Authentication auth;
    
    @Autowired
    private ObjectMapper mapper;
    
    @Test
    public void testGetConsumerById() throws Exception {
        ConsumerEntity consumer = new ConsumerEntity("consumer", "password", "consumer", "consumer@consumer.com");
        
        Mockito.when(consumerRepo.findById(1)).thenReturn(Optional.of(consumer));
        
        this.mockMvc.perform(get("/consumer/get/1")).andDo(print()).andExpect(status().isOk())
            .andExpect(content().string(containsString("\"username\":\"consumer\"")));
    }
    
    @Test
    public void testRegisterConsumer() throws Exception {
        ConsumerEntity consumer = new ConsumerEntity("consumer", "password", "consumer", "consumer@consumer.com");
        consumer.setConfirmPassword("password");
        
        mockMvc.perform(post("/consumer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(consumer))
        ).andDo(print()).andExpect(status().isOk());
        
        Mockito.verify(userService).saveUser(Matchers.any(ConsumerEntity.class));
    }
}