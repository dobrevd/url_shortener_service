package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.redis.UrlCacheService;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Setter
public class UrlService {
    @Value("${app.short_url_prefix}")
    private String shortUrlPrefix;
    private final UrlCacheService urlCacheService;
    private final LocalCache localCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public String saveAndGetShortUrl(UrlDto urlDto){
        var savedUrlWithHash = saveUrlWithHash(urlDto);
        saveUrlInCache(savedUrlWithHash);

        return savedUrlWithHash.getHash();
    }

    private void saveUrlInCache(Url savedUrlWithHash) {
        var urlHash = urlMapper.toUrlHash(savedUrlWithHash);
        urlCacheService.saveUrlHash(urlHash);
    }

    public String getUrl(String shortUrl) {
        var hash = shortUrl.substring(shortUrlPrefix.length());
        return urlCacheService.getUrl(hash);
    }

    private Url saveUrlWithHash(UrlDto urlDto){
        var hash = localCache.getHash();
        var url = urlMapper.toEntity(urlDto);
        url.setHash(shortUrlPrefix + hash);

        return urlRepository.save(url);
    }
}