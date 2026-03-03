package ru.practicum.messages;

public class Messages {
    //log
    public static final String HUB_EVENT = "Получено HUBS событие типа: {}";
    public static final String HUB_EVENT_OK = "HUBS событие успешно обработано";
    public static final String HUB_EVENT_NOT_FOUND = "HUB обработчик не найден для типа: {}";
    public static final String SENSOR_EVENT = "Получено SENSOR событие типа: {}";
    public static final String SENSOR_EVENT_OK = "SENSOR событие успешно обработано";
    public static final String SENSOR_EVENT_NOT_FOUND = "SENSOR обработчик не найден для типа: {}";
    public static final String KAFKA_INITIAL = "Инициализация KafkaConfig: {}";
    public static final String PRODUCER_INITIAL_SETTINGS = "Инициализация KafkaProducer с настройками: {}";
    public static final String PRODUCER_MESSAGE_SEND = "Сообщение отправлено в партицию {} с offset {}";
    public static final String ERROR_EVENT_KAFKA = "Ошибка при обработке события {}";
    //exception
    public static final String EXCEPTION_HUB_NOT_FOUND = "HUB обработчик не найден";
    public static final String EXCEPTION_SENSOR_NOT_FOUND = "SENSOR обработчик не найден";
    public static final String ERROR_SEND_MESSAGE = "Ошибка при отправке сообщения";
    public static final String PRODUCER_CLOSED = "Producer успешно закрыт";
    public static final String PRODUCER_NOT_CLOSED = "Producer успешно закрыт";

}
