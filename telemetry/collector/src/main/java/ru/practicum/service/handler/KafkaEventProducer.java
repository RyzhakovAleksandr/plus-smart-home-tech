package ru.practicum.service.handler;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import ru.practicum.messages.Messages;
import ru.practicum.config.KafkaConfig;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
@Getter
@Setter
@ToString
@Component
public class KafkaEventProducer {

    private Producer<String, SpecificRecordBase> producer;

    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        Properties properties = kafkaConfig.kafkaProperties();
        log.info(Messages.PRODUCER_INITIAL_SETTINGS, properties);
        this.producer = new KafkaProducer<>(properties);
    }

    public Future<RecordMetadata> sendRecord(ProducerRecord<String, SpecificRecordBase> record) {
        try {
            Future<RecordMetadata> future = producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error(Messages.ERROR_SEND_MESSAGE, exception);
                } else {
                    log.info(Messages.PRODUCER_MESSAGE_SEND,
                            metadata.partition(), metadata.offset());
                }
            });
            return future;
        } catch (Exception e) {
            throw new RuntimeException(Messages.ERROR_SEND_MESSAGE, e);
        }
    }

    public void close(Duration timeout) {
        try {
            producer.close(timeout);
            log.info(Messages.PRODUCER_CLOSED);
        } catch (Exception e) {
            log.error(Messages.PRODUCER_NOT_CLOSED, e);
        }
    }

    @PreDestroy
    public void destroy() {
        close(Duration.ofSeconds(30));
    }
}
