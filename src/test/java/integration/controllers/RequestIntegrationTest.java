package integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import meeseeks.box.MeeseeksBox;
import meeseeks.box.domain.ProviderEntity;
import meeseeks.box.domain.RequestEntity;
import meeseeks.box.repository.ProviderRepository;
import meeseeks.box.repository.RequestRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.function.Supplier;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MeeseeksBox.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:integrationtests.properties")
public class RequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ProviderRepository providerRepository;

    private ProviderEntity provider;

    @Before
    public void setUp() throws Exception {
        provider = new ProviderEntity();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(provider, null, provider.getAuthorities()));
    }

    @Test
    public void isDeletingRequestById() throws Exception {
        RequestEntity request = makeRequest("test", true);
        // preconditions:
        request.setProvider(provider);
        requestRepository.save(request);
        // then:
        assertValidResultFrom(() -> delete("/request/delete/1"), status().isOk());
    }

    @Test
    public void isUpdatingRequestMessage() throws Exception {
        // declarations:
        RequestEntity expected = makeRequest("test", true);
        // preconditions:
        expected.setProvider(provider);
        requestRepository.save(expected);
        // then:
        assertValidResultFrom(()-> patch("/request/update/1/NewText"),
                jsonPath("$.message", is("NewText")));
    }

    @Test
    public void isGettingById() throws Exception {
        RequestEntity request = makeRequest("test", true);
        // preconditions:
        request.setProvider(provider);
        requestRepository.save(request);
        // then:
        assertValidResultFrom(() -> get("/request/get/1"),
                content().json(mapper.writeValueAsString(requestRepository.findOne(1))));
    }

    private RequestEntity makeRequest(final String message, final Boolean accepted) {
        return new RequestEntity(message, accepted);
    }

    private void assertValidResultFrom(final Supplier<MockHttpServletRequestBuilder> method,
                                       final ResultMatcher matcher) throws Exception {
        mockMvc.perform(method.get()
                .principal(new UsernamePasswordAuthenticationToken(provider, null, provider.getAuthorities())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(matcher);
    }
}