package faang.school.urlshortenerservice.redis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static faang.school.urlshortenerservice.util.TestDataFactory.HASH;
import static faang.school.urlshortenerservice.util.TestDataFactory.createUrlHash;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheServiceTest {
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @InjectMocks
    private UrlCacheService urlCacheService;

    @Test
    void givenValidHash_WhenGetUrl_ThenReturnUrl() {
        // given - precondition
        var urlHash = createUrlHash();
        when(urlCacheRepository.getUrl(HASH)).thenReturn(Optional.of(urlHash));

        // when - action
        var actualResult = urlCacheService.getUrl(HASH);

        // then - verify the output
        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(urlHash.getUrl());
    }
}