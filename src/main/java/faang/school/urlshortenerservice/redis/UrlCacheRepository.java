package faang.school.urlshortenerservice.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    public static final String HASH_KEY = "url";

    private final RedisTemplate<String, UrlHash> template;

    public void save(UrlHash url){
        log.info("Saving URL cache: {}", url);
        template.opsForHash().put(HASH_KEY, url.getHash(), url);
    }

    public Optional<UrlHash> getUrl(String hash){
        var urlHash = Optional.ofNullable((UrlHash) template.opsForHash().get(HASH_KEY, hash));
        log.info("Retrieved from Redis UrlHash: {}", urlHash);
        return urlHash;
    }
}