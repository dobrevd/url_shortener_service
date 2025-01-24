package faang.school.urlshortenerservice.redis;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlCacheService {
    private final UrlCacheRepository urlCacheRepository;

    public void saveUrlHash(UrlHash url){
        urlCacheRepository.save(url);
    }

    public String getUrl(String hash){
        return urlCacheRepository.getUrl(hash)
                .map(UrlHash::getUrl)
                .orElseThrow(() -> new EntityNotFoundException("Url is not found"));
    }
}