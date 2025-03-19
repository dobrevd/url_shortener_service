package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.redis.UrlCacheService;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static faang.school.urlshortenerservice.util.TestDataFactory.HASH;
import static faang.school.urlshortenerservice.util.TestDataFactory.SHORT_URL;
import static faang.school.urlshortenerservice.util.TestDataFactory.SHORT_URL_PREFIX;
import static faang.school.urlshortenerservice.util.TestDataFactory.URL;
import static faang.school.urlshortenerservice.util.TestDataFactory.createUrl;
import static faang.school.urlshortenerservice.util.TestDataFactory.createUrlDto;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private LocalCache localCache;
    @Mock
    private UrlCacheService urlCacheService;
    @Mock
    private UrlMapper urlMapper;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlCacheService, localCache, urlRepository, urlMapper);
        urlService.setShortUrlPrefix(SHORT_URL_PREFIX);
    }

    @Test
    void givenUrlWhenSaveAndGetShortUrlThenReturnHash() {
        // given - precondition
        var url = createUrl();
        var urlDto = createUrlDto();

        when(localCache.getHash()).thenReturn(HASH);
        when(urlMapper.toEntity(urlDto)).thenReturn(url);
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        // when - action
        var actualResult = urlService.saveAndGetShortUrl(urlDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(SHORT_URL);
    }

    @Test
    void givenShortUrlWhenGetUrlThenReturnUrlFromCache() {
        // given - precondition
        when(urlCacheService.getUrl(HASH)).thenReturn(of(URL));
        when(urlRepository.findById(SHORT_URL)).thenReturn((Optional.of(createUrl())));

        // when - action
        var actualResult = urlService.getUrl(SHORT_URL);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(URL);

        verify(urlCacheService, times(1)).getUrl(HASH);
        verify(urlRepository, times(1)).findById(SHORT_URL);
    }
    @Test
    void givenShortUrlWhenGetUrlThenReturnUrlFromDataBase() {
        // given - precondition
        var url = createUrl();

        when(urlCacheService.getUrl(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findById(SHORT_URL)).thenReturn(of(url));

        // when - action
        var actualResult = urlService.getUrl(SHORT_URL);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(URL);

        verify(urlCacheService, times(1)).getUrl(HASH);
        verify(urlRepository, times(1)).findById(SHORT_URL);
    }

    @Test
    void givenInvalidShortUrlWhenGetUrlThenThrowException() {
        // given - precondition
        when(urlCacheService.getUrl(HASH)).thenReturn(null);
        when(urlRepository.findById(SHORT_URL)).thenReturn(empty());

        // when - action
        // then - verify the output
        assertThatThrownBy(() -> urlService.getUrl(SHORT_URL))
                .hasMessageContaining("Url is not found")
                        .isInstanceOf(EntityNotFoundException.class);

        verify(urlCacheService, times(1)).getUrl(HASH);
        verify(urlRepository, times(1)).findById(SHORT_URL);
    }
}