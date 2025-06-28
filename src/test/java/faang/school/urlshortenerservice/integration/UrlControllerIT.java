package faang.school.urlshortenerservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import static faang.school.urlshortenerservice.util.TestDataFactory.createUrlDto;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

@Tag("integration")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UrlControllerIT extends AbstractionBaseIT {

    @Value("${test.x-user-id-header}")
    private String xUserIdHeader;

    @Value("${test.user-id}")
    private String userId;
    @MockBean
    private SnsClient snsClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidUrlWhenSaveAndGetShortUrlThenReturnShortUrl() throws Exception {
        // given - precondition
        var urlDto = createUrlDto();
        var urlDtoJson = objectMapper.writeValueAsString(urlDto);

        when(snsClient.publish(any(PublishRequest.class)))
                .thenReturn(PublishResponse.builder().messageId("abc-123").build());

        // when - action
        var response = mockMvc.perform(post("/api")
                .contentType("application/json")
                .content(urlDtoJson)
                .header(xUserIdHeader, userId)
        );

        // then - verify the output
        response.andExpect(status().isCreated())
                .andExpect(content().string(Matchers.matchesPattern("^my_short_url/[a-zA-Z0-9]+$")))
                .andDo(print());
    }
}