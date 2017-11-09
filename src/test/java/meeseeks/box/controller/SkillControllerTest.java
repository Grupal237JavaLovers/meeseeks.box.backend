package meeseeks.box.controller;

import meeseeks.box.domain.SkillEntity;
import meeseeks.box.repository.SkillRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SkillController.class)
public class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkillRepository skillRepository;

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
}