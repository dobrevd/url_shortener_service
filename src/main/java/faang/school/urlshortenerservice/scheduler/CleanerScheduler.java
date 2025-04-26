package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    @Value("${app.scheduling.removal.months_before_expiration}")
    private int monthsBeforeExpiration;

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashMapper hashMapper;

    @Scheduled(cron = "${app.scheduling.removal.cron}")
    @Transactional
    public void removeExpiredUrls(){
        var expirationCutoff = LocalDateTime.now().minusMonths(monthsBeforeExpiration);
        var deletedHashes = urlRepository.deleteUrlsOlderThan(expirationCutoff);

        if (!deletedHashes.isEmpty()) {
            hashRepository.saveAll(deletedHashes.stream()
                    .map(hashMapper::toEntity)
                    .toList());
            log.info("Removed {} expired URLs and saved {} hashes back to database.", deletedHashes.size(), deletedHashes.size());
        } else {
            log.info("No expired URLs found for removal at {}", expirationCutoff);
        }
    }
}