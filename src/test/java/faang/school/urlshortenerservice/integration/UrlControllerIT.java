package faang.school.urlshortenerservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static faang.school.urlshortenerservice.util.TestDataFactory.createUrlDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class UrlControllerIT extends AbstractionBaseIT {
    @Value("${test.x-user-id-header}")
    private String X_USER_ID_HEADER;
    @Value("${test.user-id}")
    private String USER_ID;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidUrlWhenSaveAndGetShortUrlThenReturnShortUrl() throws Exception {
        // given - precondition
        var urlDto = createUrlDto();
        var urlDtoJson = objectMapper.writeValueAsString(urlDto);

        // when - action
        var response = mockMvc.perform(post("/api")
                .contentType("application/json")
                .content(urlDtoJson)
                .header(X_USER_ID_HEADER, USER_ID)
        );

        // then - verify the output
        response.andExpect(status().isCreated())
                .andExpect(content().string(Matchers.matchesPattern("^my_short_url/[a-zA-Z0-9]+$")))
                .andDo(print());
    }
}