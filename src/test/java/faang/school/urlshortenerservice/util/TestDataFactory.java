package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.redis.UrlHash;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public final class TestDataFactory {
    public static final int BATCH_SIZE = 10;
    private static final LocalDateTime CREATED = LocalDateTime.now().minusWeeks(1);
    public static final String HASH = "abc123";
    public static final String URL = "http://example.com/1";
    public static final String SHORT_URL_PREFIX = "my_short_url/";
    public static final String SHORT_URL = SHORT_URL_PREFIX + HASH;
    public static final String HASH_KEY = "url";

    public static Url createUrl(){
        return Url.builder()
                .hash(HASH)
                .url(URL)
                .createdAt(CREATED)
                .build();
    }
    public static UrlDto createUrlDto(){
        return UrlDto.builder()
                .url(URL)
                .createdAt(CREATED)
                .build();
    }

    public static UrlHash createUrlHash(){
        return UrlHash.builder()
                .hash(HASH)
                .url(URL)
                .build();
    }

}
