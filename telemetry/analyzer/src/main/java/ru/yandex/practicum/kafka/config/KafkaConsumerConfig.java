package ru.yandex.practicum.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Autowired
    private Environment env;

    @Bean
    public Consumer<String, SensorsSnapshotAvro> getSnapshotConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("kafka.snapshot-config.bootstrap-servers", "localhost:9092"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                env.getProperty("kafka.snapshot-config.key-deserializer", StringDeserializer.class.getName()));
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                env.getProperty("kafka.snapshot-config.value-deserializer"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG,
                env.getProperty("kafka.snapshot-config.group-id", "analyzer-snapshot-group"));
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                env.getProperty("kafka.snapshot-config.enable-auto-commit", "false"));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        log.info("Создан Snapshot consumer с настройками: {}", config);
        return new KafkaConsumer<>(config);
    }

    @Bean
    public Consumer<String, HubEventAvro> getHubEventConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("kafka.hub-event-config.bootstrap-servers", "localhost:9092"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                env.getProperty("kafka.hub-event-config.key-deserializer", StringDeserializer.class.getName()));
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                env.getProperty("kafka.hub-event-config.value-deserializer"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG,
                env.getProperty("kafka.hub-event-config.group-id", "analyzer-hub-group"));
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                env.getProperty("kafka.hub-event-config.enable-auto-commit", "true"));
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
                env.getProperty("kafka.hub-event-config.auto-commit-interval-ms", "5000"));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        log.info("Создан HubEvent consumer с настройками: {}", config);
        return new KafkaConsumer<>(config);
    }
}