package ru.practicum.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.sensor.MotionSensorEvent;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.model.sensor.enums.SensorEventType;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Slf4j
@Component
public class MotionSensorHandler extends BaseSensorHandler {
    public MotionSensorHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        super(kafkaProducer, kafkaConfig);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    public MotionSensorAvro mapToAvro(SensorEvent sensorEvent) {
        MotionSensorEvent motionSensorEvent = (MotionSensorEvent) sensorEvent;

        Boolean originalValue = motionSensorEvent.getMotion();
        boolean isMotion = Boolean.TRUE.equals(originalValue);

        log.debug("MotionSensor: id={}, original isMotion={}, mapped to={}",
                motionSensorEvent.getId(), originalValue, isMotion);

        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensorEvent.getLinkQuality() != null ? motionSensorEvent.getLinkQuality() : 0)
                .setMotion(Boolean.TRUE.equals(motionSensorEvent.getMotion()))
                .setVoltage(motionSensorEvent.getVoltage() != null ? motionSensorEvent.getVoltage() : 0)
                .build();
    }
}
