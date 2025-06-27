package faang.school.urlshortenerservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.kafka.EventType;
import faang.school.urlshortenerservice.kafka.UrlEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlEventService {
    private final KafkaTemplate<String, UrlEvent> kafkaTemplate;
    private final UserContext userContext;
    @Value("${spring.kafka.url-shortener-event-topic}")
    private String productCreatedTopic;
    @Value("${aws.sns.topic}")
    private String urlEventSnsTopic;
    private final SnsClient snsClient;

    public void sendEvent(String shortUrl, String originalUrl, EventType eventType) {
        var event = createEvent(shortUrl, originalUrl, eventType);
        kafkaTemplate.send(productCreatedTopic, event.eventId(), event);
        log.info("Event id: {} is sent", event.eventId());
    }

    public void sendEventToSns(String shortUrl, String originalUrl, EventType eventType){
        var event = createEvent(shortUrl, originalUrl, eventType);
        String messageBody ="";
        try {
            messageBody = new ObjectMapper().writeValueAsString(event);
            log.info("SNS: Event id {} sent", event.eventId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UrlEvent for SNS", e);
        }

        PublishRequest request = PublishRequest.builder()
                .topicArn(urlEventSnsTopic)
                .message(messageBody)
                .build();

        snsClient.publish(request);
    }

    private UrlEvent createEvent(String shortUrl, String originalUrl, EventType eventType) {
        var eventId = UUID.randomUUID().toString();
        var userId = userContext.getUserId();

        return UrlEvent.builder()
                .eventId(eventId)
                .userId(userId)
                .shortUrlHash(shortUrl)
                .originalUrl(originalUrl)
                .eventType(eventType)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }
}