package meeseeks.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.notNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SkillController.class)
@ContextConfiguration
public class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkillRepository skillRepository;

    @MockBean
    private ProviderRepository providerRepository;

    @Autowired
    private ObjectMapper mapper;

    private ProviderEntity principal;

    @Before
    public void setupAuthentication() {
        this.principal = new ProviderEntity("test", "test", "Test", "test@test.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, AuthorityUtils.createAuthorityList("ROLE_PROVIDER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void isFindingSkillsByName() throws Exception {
        // declarations:
        List<SkillEntity> expected = singletonList(new SkillEntity("testing"));
        // when:
        Mockito.when(skillRepository.findAllByNameContaining("test", new PageRequest(0, 10)))
                .thenReturn(expected);
        // then:
        mockMvc.perform(get("/skill/get/test/10")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":0,\"name\":\"testing\"}]"));
    }

    @Test
    public void isGettingSkillById() throws Exception {
        // declarations:
        SkillEntity expected = new SkillEntity(1, "test");
        // when:
        Mockito.when(skillRepository.findById(1)).thenReturn(Optional.of(expected));
        // then:
        mockMvc.perform(get("/skill/get/1")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"name\":\"test\"}"));
    }

    @Test
    @WithMockUser(username = "test", roles = {"PROVIDER"})
    public void isInsertingSkillToProvider() throws Exception {
        // when:
        Mockito.when(skillRepository.findByName("design")).thenReturn(Optional.empty());
        Mockito.when(providerRepository.save((ProviderEntity)notNull())).thenReturn(this.principal);
        // then:
        mockMvc.perform(get("/skill/insert/design"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(singletonList(new SkillEntity("design")))));
    }

    @Test
    @WithMockUser(username = "test", roles = {"PROVIDER"})
    public void isDeletingSkillFromProvider() throws Exception {
        // declarations:
        SkillEntity skill = new SkillEntity("test");
        skill.getProviders().add(this.principal);
        this.principal.getSkills().add(skill);
        // when:
        Mockito.when(skillRepository.findById(1)).thenReturn(Optional.of(skill));
        Mockito.when(providerRepository.save((ProviderEntity)notNull())).thenReturn(this.principal);
        // then:
        mockMvc.perform(get("/skill/delete/1")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[]"));
        assertTrue(this.principal.getSkills().isEmpty());
    }
}