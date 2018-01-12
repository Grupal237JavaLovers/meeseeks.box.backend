
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.function.Supplier;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author Alexandru Stoica
 */

public class AssertRequest {

    private MockMvc mvc;

    public AssertRequest(final MockMvc mockMvc) {
        this.mvc = mockMvc;
    }

    public void assertExpectedResultEquals(
            final RequestBuilder request,
            final Supplier<ResultMatcher> matcher) throws Exception {
        performRequestAndPrintResult(request)
                .andExpect(matcher.get());
    }

    private ResultActions performRequestAndPrintResult(
            final RequestBuilder request) throws Exception {
        return mvc.perform(request).andDo(print());
    }
}
