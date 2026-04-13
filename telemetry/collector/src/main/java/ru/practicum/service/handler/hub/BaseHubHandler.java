package ru.practicum.service.handler.hub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.messages.Messages;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.hub.HubEvent;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.practicum.service.mapper.hub.HubEventAvroMapper;
import ru.practicum.service.mapper.hub.HubEventProtoMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseHubHandler implements HubEventHandler {

    KafkaEventProducer producer;
    String topic;
    final HubEventAvroMapper avroMapper;
    final HubEventProtoMapper protoMapper;

    protected abstract HubEventAvro mapHubToAvro(HubEvent hubEvent);
    protected abstract HubEvent mapHubProtoToModel(HubEventProto hubProto);

    public BaseHubHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig,  HubEventAvroMapper avroMapper, HubEventProtoMapper protoMapper) {
        this.producer = kafkaProducer;
        topic = kafkaConfig.getTopics().get("hubs-events");
        this.avroMapper = avroMapper;
        this.protoMapper = protoMapper;
        if (topic == null) {
            throw new IllegalArgumentException(Messages.ERROR_NOT_TOPIC_HUB);
        }
    }

    @Override
    public void handle(HubEventProto hubProto) {
        if (hubProto == null) {
            throw new IllegalArgumentException(Messages.EXCEPTION_HUB_NOT_FOUND);
        }
        try {
            HubEvent event = mapHubProtoToModel(hubProto);
            log.trace(Messages.HUB_MAP, event.getHubId());

            HubEventAvro avro = mapHubToAvro(event);
            log.trace(Messages.HUB_MAP_TO_AVRO, event.getHubId());

            sendToKafka(avro, event.getHubId(), event.getTimestamp());

        } catch (Exception e) {
            log.error(Messages.HUB_EVENT_NOT_FOUND, hubProto.getPayloadCase(), e);
            throw new RuntimeException(Messages.EXCEPTION_HUB_NOT_FOUND, e);
        }
    }

    protected void sendToKafka(HubEventAvro avro, String hubId, Instant timestamp) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timestamp.toEpochMilli(),
                hubId,
                avro
        );

        producer.sendRecord(record);
        log.info(Messages.HUB_EVENT_SENT, hubId, topic);
    }

    protected HubEventAvro buildHubEventAvro(HubEvent hubEvent, SpecificRecordBase payloadAvro) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payloadAvro)
                .build();
    }

    protected HubEvent mapBaseHubProtoFieldsToHub(HubEvent hub, HubEventProto hubProto) {
        hub.setHubId(hubProto.getHubId());

        long seconds = hubProto.getTimestamp().getSeconds();
        int nanos = hubProto.getTimestamp().getNanos();

        hub.setTimestamp(Instant.ofEpochSecond(seconds, nanos));
        return hub;
    }
}
