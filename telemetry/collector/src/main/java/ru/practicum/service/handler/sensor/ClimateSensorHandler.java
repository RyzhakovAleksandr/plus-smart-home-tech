package ru.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.model.sensor.ClimateSensorEvent;
import ru.practicum.model.sensor.SensorEvent;
import ru.practicum.model.sensor.enums.SensorEventType;
import ru.practicum.service.handler.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
public class ClimateSensorHandler extends BaseSensorHandler {
    public ClimateSensorHandler(KafkaEventProducer kafkaProducer, KafkaConfig kafkaConfig) {
        super(kafkaProducer, kafkaConfig);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public ClimateSensorAvro mapToAvro(SensorEvent sensorEvent) {
        ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) sensorEvent;

        Integer tempC = climateSensorEvent.getTemperatureC();
        Integer hum = climateSensorEvent.getHumidity();
        Integer co2 = climateSensorEvent.getCo2Level();

        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(tempC != null ? tempC : 0)
                .setHumidity(hum != null ? hum : 0)
                .setCo2Level(co2 != null ? co2 : 0)
                .build();
    }
}
