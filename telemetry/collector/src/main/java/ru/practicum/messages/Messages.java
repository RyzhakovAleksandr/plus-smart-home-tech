package ru.practicum.messages;

public class Messages {
    //log
    public static final String HUB_EVENT_SENT = "Hub событие отправлено в Kafka. hubId={}, topic={}";
    public static final String HUB_EVENT_NOT_FOUND = "HUB обработчик не найден для типа: {}";
    public static final String HUB_MAP = "маппинг в данные HUB с индификатором={}";
    public static final String HUB_MAP_TO_AVRO = "маппинг в AVRO данные HUB с индификатором={}";
    public static final String SENSOR_EVENT_SENT = "Сенсор событие отправлено в Kafka. sensorId={}, topic={}";
    public static final String SENSOR_MAP = "маппинг в данные SENSOR с индификатором={}";
    public static final String SENSOR_MAP_TO_AVRO = "маппинг в AVRO данные SENSOR с индификатором={}";
    public static final String KAFKA_INITIAL = "Инициализация KafkaConfig: {}";
    public static final String PRODUCER_INITIAL_SETTINGS = "Инициализация KafkaProducer с настройками: {}";
    public static final String PRODUCER_MESSAGE_SEND = "Сообщение отправлено в партицию {} с offset {}";
    public static final String ERROR_EVENT_KAFKA = "Ошибка при обработке события {}";
    public static final String ERROR_NOT_TOPIC_SENSOR = "Топик sensors-events не найден в конфигурации!";
    public static final String ERROR_NOT_TOPIC_HUB = "Топик hub-events не найден в конфигурации!";
    //exception
    public static final String EXCEPTION_HUB_NOT_FOUND = "HUB обработчик не найден";
    public static final String EXCEPTION_SENSOR_NOT_FOUND = "SENSOR обработчик не найден";
    public static final String ERROR_SEND_MESSAGE = "Ошибка при отправке сообщения";
    public static final String PRODUCER_CLOSED = "Producer успешно закрыт";
    public static final String PRODUCER_NOT_CLOSED = "Producer успешно закрыт";

}
