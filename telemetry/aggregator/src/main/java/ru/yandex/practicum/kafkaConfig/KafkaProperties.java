package ru.yandex.practicum.kafkaConfig;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProperties {
    String bootstrapServers;
    Producer producer;
    Consumer consumer;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Producer {
        String keySerializer;
        String valueSerializer;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Consumer {
        String keyDeserializer;
        String valueDeserializer;
        String groupId;
        String clientId;
        boolean enableAutoCommit;
    }
}
