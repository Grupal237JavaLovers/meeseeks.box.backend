package meeseeks.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.SkillRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SkillController.class)
@AutoConfigureMockMvc
@DisplayName("Skills Integration Tests")
public class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ProviderEntity provider;

    @MockBean
    private ProviderRepository providerRepository;

    @MockBean
    private SkillRepository skillRepository;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        provider = new ProviderEntity();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(provider, null));
    }

    @Test
    public void isGettingSkillByName() throws Exception {
        // declaration:
        List<SkillEntity> result = asList(makeSkill("A"));
        // when:
        when(skillRepository.findAllByNameContaining(anyString(), any(PageRequest.class))).thenReturn(result);
        // then:
        assertResponseIsValid(() -> get("/skill/get/A/1"), result);
    }

    @Test
    public void isGettingAllTheTasks() throws Exception {
        // declaration:
        List<SkillEntity> skills = asList(makeSkill(), makeSkill(), makeSkill());
        // when:
        when(skillRepository.getAllSkillsForCurrentProvider()).thenReturn(skills);
        // then:
        assertResponseIsValid(() -> get("/skill/get/all"), skills);
    }

    @Test
    public void isGettingById() throws Exception {
        // declaration:
        SkillEntity skill = makeSkill();
        // when:
        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));
        // then:
        assertResponseIsValid(() -> get("/skill/get/1"), skill);
    }

    @Test
    public void isInsertingSkillToProvider() throws Exception {
        // declaration:
        List<SkillEntity> skills = new ArrayList<>(asList(makeSkill("A"), makeSkill("B")));
        // preconditions:
        provider.setSkills(skills);
        // when:
        when(skillRepository.findByName("testing")).thenReturn(Optional.empty());
        when(skillRepository.getAllSkillsForCurrentProvider()).thenReturn(skills);
        when(providerRepository.save(any(ProviderEntity.class))).thenReturn(provider);
        // then:
        assertResponseIsValid(() -> post("/skill/insert/testing"), skills);
    }

    @Test
    public void isDeletingSkillFromProvider() throws Exception {
        // declarations:
        SkillEntity deletedSkillByTheEnd = makeSkill("test");
        List<SkillEntity> skills = new ArrayList<>(asList(deletedSkillByTheEnd, makeSkill("C")));
        // preconditions:
        provider.setSkills(skills);
        // when:
        when(skillRepository.getAllSkillsForCurrentProvider()).thenReturn(skills);
        when(skillRepository.findById(1)).thenReturn(Optional.of(deletedSkillByTheEnd));
        when(providerRepository.save(any(ProviderEntity.class))).thenReturn(provider);
        // then:
        assertResponseIsValid(() -> delete("/skill/delete/1"), skills);
    }

    private <T> void assertResponseIsValid(final Supplier<MockHttpServletRequestBuilder> method, final T object) throws Exception {
        mockMvc.perform(method.get()
                .principal(new UsernamePasswordAuthenticationToken(provider, null)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(object)));
    }

    private SkillEntity makeSkill(String name) {
        return new SkillEntity(name);
    }

    private SkillEntity makeSkill() {
        return new SkillEntity(1, "testing");
    }
}