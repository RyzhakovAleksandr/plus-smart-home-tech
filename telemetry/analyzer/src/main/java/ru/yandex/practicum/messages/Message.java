package ru.yandex.practicum.messages;

public class Message {
    // Успешные операции
    public static final String INFO_ACTION_SENDING = "Отправка ДЕЙСТВИЯ для сценария {}";
    public static final String INFO_ACTION_SENT = "Действие отправлено: scenario={}, sensor={}";
    public static final String INFO_HUB_MESSAGE = "Получено HUB сообщение типа: {}";
    public static final String INFO_SNAPSHOT_RECEIVED = "Получили SNAPSHOT состояния умного дома: {}";
    public static final String INFO_DEVICE_SAVING = "Сохраняем новое устройство для HUB с ID = {}";
    public static final String INFO_DEVICE_REMOVING = "Удаляем устройство из HUBa с ID = {} с hub_id = {}";
    public static final String INFO_SCENARIO_CONDITIONS_LIST = "Получили СПИСОК условий {} у сценария name = {}";
    public static final String INFO_SCENARIO_REMOVING = "Удаляем сценарий из HUB с NAME = {} и с ID = {}";
    public static final String INFO_SCENARIO_NOT_FOUND = "Сценарий не найден";
    // Ошибки
    public static final String ERROR_ACTION_SEND = "Ошибка отправки действия в Hub Router";
    public static final String ERROR_SNAPSHOT_PROCESSING = "Ошибка обработки снапшота для хаба {}";
    public static final String ERROR_KAFKA_CONSUME = "Ошибка чтения данных из топика {}";
    public static final String ERROR_OFFSET_COMMIT = "Ошибка во время фиксации offset-ов: {}";

    // Exceptions
    public static final String ERROR_NO_HANDLER = "Не найден обработчик для события типа %s";

    // Предупреждения
    public static final String WARN_CONSUMER_WOKEN = "Consumer получил сигнал wakeup, завершаем работу";
    public static final String WARN_CONSUMER_CLOSE_ERROR = "Ошибка при закрытии CONSUMER";

    // Отладка
    public static final String DEBUG_OFFSET_COMMIT = "Фиксация offset-ов: max={}";
    public static final String DEBUG_OFFSET_COMMIT_SUCCESS = "Успешная фиксация offset-ов: {}";

    // Жизненный цикл
    public static final String INFO_CONSUMER_STOPPING = "Остановка consumer";
    public static final String INFO_CONSUMER_STOPPED = "Consumer остановлен штатно";
}
