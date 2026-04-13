package ru.yandex.practicum.messages;

public class Message {
    //logs
    public static final String SCENARIO_ADDING = "Добавление сценария: hubId={}, scenarioName={}, условий={}, действий={}";
    public static final String SCENARIO_DELETING = "Удаление существующего сценария: id={}, name={}";
    public static final String SCENARIO_SAVED = "Сценарий {} успешно сохранен для хаба {}";
    public static final String SCENARIO_REMOVED = "Сценарий {} удален из хаба {}";
    public static final String SCENARIOS_FOUND = "Найдено {} сценариев для выполнения";
    public static final String PROCESSING_SCENARIO = ">>> Обработка сценария: {} (hubId: {})";
    public static final String SCENARIOS_FOUND_FOR_HUB = "Найдено {} сценариев для хаба {}";
    public static final String NO_SCENARIOS_FOUND = "Нет сценариев для выполнения";
    public static final String DEVICE_ADDED_TO_HUB = "Устройство {} добавлено в хаб {}";
    public static final String DEVICE_REMOVED_FROM_HUB = "Устройство {} удалено из хаба {}";
    public static final String ACTION_DETAILS = "Действие: sensorId={}, actionType={}, value={}";
    public static final String WAITING_FOR_MESSAGES = "Ожидание новых сообщений...";
    public static final String COMMAND_SENT = "Команда отправлена: устройство={}, действие={}, значение={}";
    public static final String CONDITION_ADDED = "Добавлено условие: sensor={}, type={}, operation={}, value={}";
    public static final String SHUTDOWN_SIGNAL_RECEIVED = "Получен сигнал завершения, инициируем остановку...";
    public static final String ANALYZING_SNAPSHOT = "Анализ снапшота: {}";
    public static final String ACTION_EXECUTE_CALLED = "actionExecute вызван с {} сценариями";
    public static final String CONSUMER_CLOSED = "Consumer закрыт";
    public static final String ANALYZER_CLOSED = "Analyzer завершил работу";

    //debug
    public static final String PROCESSING_HUB_EVENT = "Обработка хаб-ивента: topic={}, partition={}, offset={}, hubId={}";
    public static final String PROCESSING_SNAPSHOT = "Обработка снепшота: topic={}, partition={}, offset={}, hubId={}";
    public static final String ACTION_ADDED = "Добавлено действие: sensor={}, type={}, value={}";
    public static final String NO_SCENARIOS_FOR_HUB = "Для хаба {} сценарии не найдены";
    public static final String CHECKING_SCENARIO = "Проверка сценария: {}";
    public static final String CONDITION_NOT_MET = "Условие не выполнено. Датчик: {}. Тип: {}.";
    public static final String SENSOR_NOT_IN_SNAPSHOT = "Датчик {} не найден в снапшоте";
    public static final String SENSOR_LOG = "Датчик: {}, {}, {}, {}";
    public static final String SENSOR_VALUE_RETRIEVAL_FAILED = "Не удалось получить значение типа {} из датчика {}";

    //warm
    public static final String OFFSET_COMMIT_ERROR = "Ошибка во время фиксации оффсетов: {}";
    public static final String SCENARIO_NOT_FOUND = "Сценарий {} не найден в хабе {}";
    public static final String NO_EVENT_HANDLER = "Нет обработчика для типа события: {}";
    public static final String UNKNOWN_ACTION_TYPE = "Неизвестный тип действия '{}' для устройства {}. Команда не отправлена.";

    //error
    public static final String CRITICAL_ERROR = "Критическая ошибка";
    public static final String CONSUMER_CLOSE_ERROR = "Ошибка при закрытии consumer";
    public static final String MESSAGE_PROCESSING_ERROR = "Ошибка обработки сообщения offset={}: {}";
    public static final String NO_SENSOR_HANDLER = "Не найден обработчик для типа данных сенсора: {}";
    public static final String GRPC_SEND_ERROR = "Ошибка gRPC при отправке команды устройству: {}";

    //exception
    public static final String SENSOR_NOT_FOUND = "Датчик не найден: %s";
    public static final String NO_HANDLER_FOR = "Нет обработчика для %s";
}
