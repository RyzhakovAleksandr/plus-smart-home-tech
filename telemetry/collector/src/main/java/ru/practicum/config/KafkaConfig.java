package ru.practicum.config;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.messages.Messages;

import java.util.Map;
import java.util.Properties;

@Slf4j
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
@ConfigurationProperties("collector.kafka")
public class KafkaConfig {
    Map<String, String> topics;
    Map<String, String> producerProperties;

    @PostConstruct
    public void init() {
        log.info(Messages.KAFKA_INITIAL, this);
    }

    @Bean
    public Properties kafkaProperties() {
        Properties properties = new Properties();
        properties.putAll(producerProperties);
        return properties;
    }
}