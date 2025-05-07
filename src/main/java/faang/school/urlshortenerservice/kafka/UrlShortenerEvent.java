package faang.school.urlshortenerservice.kafka;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UrlShortenerEvent(
         String eventId,
         long userId,
         EventType eventType,
         String shortUrlHash,
         String originalUrl,
         Instant timestamp
) {}