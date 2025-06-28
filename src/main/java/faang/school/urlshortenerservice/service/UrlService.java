package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.kafka.EventType;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.redis.UrlCacheService;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Setter
@Slf4j
public class UrlService {
    @Value("${app.short_url_prefix}")
    private String shortUrlPrefix;
    private final UrlCacheService urlCacheService;
    private final LocalCache localCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UrlEventService urlEventService;

    public String saveAndGetShortUrl(UrlDto urlDto){
        var savedUrlWithHash = saveCreatedUrl(urlDto);
        saveUrlInCache(savedUrlWithHash);
        var shortUrl = shortUrlPrefix + savedUrlWithHash.getHash();

        urlEventService.sendEvent(shortUrl, savedUrlWithHash.getUrl(), EventType.CREATE);
        urlEventService.sendEventToSns(shortUrl, savedUrlWithHash.getUrl(), EventType.CREATE);
        return shortUrl;
    }

    public String getUrl(String shortUrl) {
        if (!shortUrl.startsWith(shortUrlPrefix)) {
            log.error("ShortUrl does not start with {}", shortUrl);
            throw new IllegalArgumentException("Invalid short URL format");
        }
        var hash = getHashFromShortUrl(shortUrl);

        var originalUrl = urlCacheService.getUrl(hash)
                .orElseGet(() -> getLongUrl(shortUrl));

        urlEventService.sendEvent(shortUrl, originalUrl, EventType.RESOLVE);
        urlEventService.sendEventToSns(shortUrl, originalUrl, EventType.RESOLVE);
        return originalUrl;
    }

    private String getHashFromShortUrl(String shortUrl) {
        return shortUrl.substring(shortUrlPrefix.length());
    }

    private void saveUrlInCache(Url savedUrlWithHash) {
        var urlHash = urlMapper.toUrlHash(savedUrlWithHash);
        urlCacheService.saveUrlHash(urlHash);
    }

    private String getLongUrl(String shortUrl) {
        var hash = getHashFromShortUrl(shortUrl);
        log.info("Retrieving from Postgres longUrl with hash: {}", hash);

        return urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new EntityNotFoundException("Url is not found"));
    }

    private Url saveCreatedUrl(UrlDto urlDto){
        var url = createUrl(urlDto);
        log.info("Saving url in Postgres: {}", url);
        return urlRepository.save(url);
    }

    private Url createUrl(UrlDto urlDto) {
        var hash = localCache.getHash();
        var url = urlMapper.toEntity(urlDto);
        url.setHash(hash);
        url.setCreatedAt(LocalDateTime.now());
        return url;
    }
}