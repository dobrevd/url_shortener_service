package faang.school.urlshortenerservice.kafka;

import lombok.Builder;

@Builder
public record UrlShortenerEvent(
         String eventId,
         long userId,
         EventType eventType,
         String shortUrlHash,
         String originalUrl,
         long timestamp
) {}