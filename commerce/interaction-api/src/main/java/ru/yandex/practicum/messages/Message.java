package ru.yandex.practicum.messages;

public class Message {
    //Exceptions
    public static final String PRODUCT_ID_MUST_BE = "Идентификационный номер товара не указан";
    public static final String PRODUCT_NOT_FOUND = "Товар с идентификационным номером %s не найден";
    public static final String HANDLER_PRODUCT_NOT_FOUND_EXCEPTION = "ProductNotFoundException exception: {}";
    public static final String HANDLER_ILLEGAL_ARGUMENT_EXCEPTION = "IllegalArgumentException exception: {}";
    public static final String HANDLER_METHOD_NOT_VALID_EXCEPTION = "MethodArgumentNotValidException exception: {}";
    public static final String HANDLER_CONSTRAINT_VIOLATION_EXCEPTION = "ConstraintViolationException exception: {}";
    public static final String HANDLER_ERROR = "Unexpected error: {}";


    // Info level
    public static final String CREATING_PRODUCT = "Создание нового товара: {}";
    public static final String PRODUCT_CREATED_SUCCESS = "Товар успешно создан. ID: {}, Название: {}";
    public static final String UPDATING_PRODUCT = "Обновление товара с ID: {}";
    public static final String PRODUCT_UPDATED_SUCCESS = "Товар с ID {} успешно обновлен";
    public static final String DEACTIVATING_PRODUCT = "Деактивация товара с ID: {}";
    public static final String PRODUCT_DEACTIVATED_SUCCESS = "Товар с ID {} успешно деактивирован";
    public static final String UPDATING_QUANTITY_STATE = "Обновление количества товара {} до состояния: {}";
    public static final String QUANTITY_STATE_UPDATED_SUCCESS = "Количество товара {} обновлено до: {}";
    public static final String FOUND_PRODUCTS_COUNT = "Найдено товаров: {}, категория: {}";

    // Debug level
    public static final String FETCHING_PRODUCTS_BY_CATEGORY = "Запрос товаров по категории: {}";
    public static final String FETCHING_PRODUCT_BY_ID = "Поиск товара по ID: {}";

    // Warn level
    public static final String PRODUCT_ALREADY_DEACTIVATED = "Товар с ID {} уже был деактивирован";
    public static final String QUANTITY_STATE_UPDATE_FAILED = "Не удалось обновить количество товара с ID {}";

    // Error level
    public static final String PRODUCT_NOT_ACTIVE = "Товар с ID {} неактивен";
}
