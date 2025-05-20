package faang.school.urlshortenerservice.kafka;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlEventService {
    private final KafkaTemplate<String, UrlEvent> kafkaTemplate;
    private final UserContext userContext;
    private final AmazonSQSAsync amazonSQSAsync;
    @Value("${spring.kafka.url-shortener-event-topic}")
    private String productCreatedTopic;
    @Value("${aws.sqs.queue}")
    private String urlEventQueueUrl;

    public void sendEvent(String shortUrl, String originalUrl, EventType eventType) {
        var event = createEvent(shortUrl, originalUrl, eventType);
        kafkaTemplate.send(productCreatedTopic, event.eventId(), event);
        log.info("Event id: {} is sent", event.eventId());
    }

    public void sendEventToSqs(String shortUrl, String originalUrl, EventType eventType){
        var event = createEvent(shortUrl, originalUrl, eventType);

        try {
            String messageBody = new ObjectMapper().writeValueAsString(event);
            amazonSQSAsync.sendMessage(urlEventQueueUrl, messageBody);
            log.info("SQS: Event id {} sent", event.eventId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UrlEvent for SQS", e);
        }
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