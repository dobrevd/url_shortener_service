package faang.school.urlshortenerservice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlCacheService {
    private final UrlCacheRepository urlCacheRepository;

    public void saveUrlHash(UrlHash url){
        urlCacheRepository.save(url);
    }

    public Optional<String> getUrl(String hash){
        return urlCacheRepository.getUrl(hash)
                .map(UrlHash::getUrl);
    }
}