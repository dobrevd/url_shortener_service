package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.kafka.UrlEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    @Value("${spring.kafka.url-shortener-event-topic}")
    private String urlShortenerEventTopic;
    @Value("${spring.kafka.topic-replication-factor}")
    private Integer topicReplicationFactor;
    @Value("${spring.kafka.topic-partitions}")
    private Integer topicPartitions;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.acks}")
    private String acks;
    @Value("${spring.kafka.producer.delivery-timeout}")
    private Integer deliveryTimeout;
    @Value("${spring.kafka.producer.linger}")
    private Integer linger;
    @Value("${spring.kafka.producer.request-timeout}")
    private Integer requestTimeout;
    @Value("${spring.kafka.producer.idempotence}")
    private boolean idempotence;
    @Value("${spring.kafka.producer.inflight-requests}")
    private Integer inflightRequests;

    Map<String, Object> producerConfigs() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        config.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, inflightRequests);

        return config;
    }

    @Bean
    ProducerFactory<String, UrlEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, UrlEvent> kafkaTemplate(ProducerFactory<String, UrlEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic urlShortenerEvent(){
        return TopicBuilder.name(urlShortenerEventTopic)
                .partitions(topicPartitions)
                .replicas(topicReplicationFactor)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }
}