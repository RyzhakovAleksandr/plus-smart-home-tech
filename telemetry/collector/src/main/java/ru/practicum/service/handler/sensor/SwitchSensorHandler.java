package ru.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.model.sensor.SwitchSensorEvent;
import ru.practicum.model.sensor.enums.SensorEventType;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorHandler extends BaseSensorHandler {
    public SwitchSensorHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        super(kafkaProducer, kafkaConfig);
    }

    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    public SwitchSensorAvro mapToAvro(SensorEvent sensorEvent) {
        SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) sensorEvent;

        return SwitchSensorAvro.newBuilder()
                .setState(Boolean.TRUE.equals(switchSensorEvent.getState()))
                .build();
    }
}
