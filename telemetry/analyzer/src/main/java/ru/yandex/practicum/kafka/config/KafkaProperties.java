package ru.yandex.practicum.kafka.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProperties {
    String bootstrapServers;
    SnapshotConfig snapshotConfig;
    HubEventConfig hubEventConfig;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SnapshotConfig {
        String groupId;
        String keyDeserializer;
        String valueDeserializer;
        boolean enableAutoCommit;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class HubEventConfig {
        String groupId;
        String keyDeserializer;
        String valueDeserializer;
        boolean enableAutoCommit;
        String autoCommitIntervalMs;
    }
}
