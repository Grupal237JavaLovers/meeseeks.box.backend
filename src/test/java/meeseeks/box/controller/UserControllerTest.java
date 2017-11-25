package meeseeks.box.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.service.UserService;

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

    private ProviderEntity principal;

    @Before
    public void setupAuthentication() {
        this.principal = new ProviderEntity("test", "test", "Test", "test@test.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, AuthorityUtils.createAuthorityList("ROLE_PROVIDER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(username = "test", roles = {"PROVIDER"})
    public void deleteUser() throws Exception {
        // when:
        Mockito.when(userService.delete("test")).thenReturn(true);
        // then:
        mockMvc.perform(get("/user/delete"))
                .andDo(print()).andExpect(status().isAccepted());
    }
}