package meeseeks.box.controller;

import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import meeseeks.box.security.SecurityConstants;
import meeseeks.box.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    public void isInsertingSkillToProvider() throws Exception {
        // declarations:
        ProviderEntity provider = new ProviderEntity("test", "test", "Test", "test@test.com");
        // when:
        Mockito.when(providerRepository.findById(1)).thenReturn(Optional.of(provider));
        Mockito.when(skillRepository.findByName("design")).thenReturn(Optional.empty());
        Mockito.when(providerRepository.save(provider)).thenReturn(provider);
        // then:
        mockMvc.perform(get("/provider/get/1/skills/add/design")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":0,\"name\":\"design\"}]"));
    }

    @Test
    public void isDeletingSkillFromProvider() throws Exception {
        // declarations:
        ProviderEntity provider = new ProviderEntity("test", "test", "Test", "test@test.com");
        SkillEntity skill = new SkillEntity("test");
        provider.getSkills().add(skill);
        // when:
        Mockito.when(providerRepository.findById(1)).thenReturn(Optional.of(provider));
        Mockito.when(skillRepository.findById(1)).thenReturn(Optional.of(skill));
        Mockito.when(providerRepository.save(provider)).thenReturn(provider);
        // then:
        mockMvc.perform(get("/provider/get/1/skills/delete/1")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[]"));
        assertTrue(provider.getSkills().isEmpty());
    }
}