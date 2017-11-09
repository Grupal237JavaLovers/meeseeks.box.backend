package meeseeks.box.controller;

import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.UserService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(ProviderController.class)
public class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderRepository providerRepository;

    @MockBean
    private SkillRepository skillRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private SecurityConstants securityConstants;

}