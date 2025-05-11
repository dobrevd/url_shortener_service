package faang.school.urlshortenerservice.kafka;

import lombok.Builder;

@Builder
public record UrlEvent(
         String eventId,
         long userId,
         EventType eventType,
         String shortUrlHash,
         String originalUrl,
         long timestamp
) {}