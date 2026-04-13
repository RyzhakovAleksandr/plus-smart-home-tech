package ru.yandex.practicum.messages;

public class Message {
    //log
    public static final String LOG_PROCESS_NEXT = "Обрабатываем очередное сообщение {}";
    public static final String LOG_ERROR_EVENT = "Ошибка во время обработки событий от датчиков";
    public static final String LOG_CLOSE_CONSUMER = "Закрываем CONSUMER";
    public static final String LOG_CLOSE_PRODUCER = "Закрываем PRODUCER";
    public static final String LOG_RECORD_DETAILS = "топик = {}, партиция = {}, смещение = {}, значение: {}";
    public static final String LOG_SNAPSHOT_RECEIVED = "Получили снимок состояния {}";
    public static final String LOG_WRITE_TO_TOPIC = "Запись в топик Kafka";
    public static final String LOG_SNAPSHOT_UPDATED_SENT = "SNAPSHOT обновлен и отправлен {}";
    public static final String LOG_SNAPSHOT_NOT_UPDATED = "SNAPSHOT не обновлен";
    public static final String LOG_OFFSET_COMMIT_ERROR = "Ошибка во время фиксации оффсетов: {}";
}
