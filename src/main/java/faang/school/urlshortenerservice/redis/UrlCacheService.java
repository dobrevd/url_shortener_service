package faang.school.urlshortenerservice.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlCacheService {
    private final UrlCacheRepository urlCacheRepository;

    public void saveUrlHash(UrlHash url) {
        log.info("Saving UrlHash in Redis: {}", url);
        urlCacheRepository.save(url);
    }

    public Optional<String> getUrl(String hash) {
        log.info("Retrieving from Redis URL cache for hash: {}", hash);
        var url = urlCacheRepository.getUrl(hash)
                .map(UrlHash::getUrl);

        log.info("Retrieved from Redis URL: {}", url);
        return url;
    }
}