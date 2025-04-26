package faang.school.urlshortenerservice.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import static faang.school.urlshortenerservice.util.TestDataFactory.HASH;
import static faang.school.urlshortenerservice.util.TestDataFactory.HASH_KEY;
import static faang.school.urlshortenerservice.util.TestDataFactory.createUrlHash;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheRepositoryTest {
    @Mock
    private RedisTemplate<String, UrlHash> redisTemplate;
    @Mock
    private HashOperations<String, String, UrlHash> hashOperations;

    private UrlCacheRepository urlCacheRepository;

    @BeforeEach
    void setUp() {
        Mockito.<HashOperations<String, String, UrlHash>>when(redisTemplate.opsForHash())
                .thenReturn(hashOperations);
        urlCacheRepository = new UrlCacheRepository(redisTemplate);
    }

    @Test
    void givenValidHash_WhenGetUrl_ThenReturnUrlHash() {
        // given - precondition
        var expectedResult = createUrlHash();
        when(hashOperations.get(HASH_KEY, HASH)).thenReturn(expectedResult);

        // when - action
        var actualResult = urlCacheRepository.getUrl(HASH);

        // then - verify the output
        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(expectedResult);
    }
}