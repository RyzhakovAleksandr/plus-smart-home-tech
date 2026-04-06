package ru.yandex.practicum.messages;

public class Message {
    //Exceptions
    public static final String PRODUCT_ID_MUST_BE = "Идентификационный номер товара не указан";
    public static final String USERNAME_MUST_BE = "Имя пользователя не должно быть пустым";
    public static final String PRODUCT_NOT_FOUND = "Товар с идентификационным номером %s не найден";
    public static final String CART_NOT_ACTIVE = "Нет активных корзин для пользователя: %s";
    public static final String PRODUCT_NOT_FOUND_IN_CART = "Продукт не найден в корзине с id: %s";
    public static final String SERVER_UNAVAILABLE = "Сервис временно не доступен";
    public static final String WAREHOUSE_CHECK_FAILED = "Ошибка при проверке товаров на складе: %s";

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
    public static final String WAREHOUSE_REQUEST = "Запрос на склад - shoppingCartId: {}, products: {}";
    public static final String CART_NOT_EXISTS_CREATING = "Корзина не найдена для пользователя {}, создаем новую";
    public static final String CART_CREATED = "Создана новая корзина для пользователя: {}";
    public static final String ADDING_PRODUCTS_TO_CART = "Добавление товаров в корзину для пользователя: {}. Товары: {}";
    public static final String PRODUCTS_ADDED_SUCCESS = "Товары успешно добавлены в корзину. Корзина ID: {}, количество товаров: {}";
    public static final String DEACTIVATING_CART = "Деактивация корзины для пользователя: {}";
    public static final String CART_DEACTIVATED_SUCCESS = "Корзина успешно деактивирована для пользователя: {}";
    public static final String CART_DEACTIVATED_NOT_FOUND = "Не найдено активных корзин для пользователя: {}";
    public static final String REMOVING_PRODUCTS_FROM_CART = "Удаление товаров из корзины для пользователя: {}. Товары: {}";
    public static final String PRODUCTS_REMOVED_COUNT = "Удалено товаров из корзины: {}";
    public static final String CHANGING_PRODUCT_QUANTITY = "Изменение количества товара в корзине. Пользователь: {}, товар: {}, новое количество: {}";
    public static final String QUANTITY_CHANGED_SUCCESS = "Количество товара успешно обновлено в корзине";

    // Debug level
    public static final String GETTING_CART = "Получение корзины для пользователя: {}";
    public static final String FETCHING_PRODUCTS_BY_CATEGORY = "Запрос товаров по категории: {}";
    public static final String FETCHING_PRODUCT_BY_ID = "Поиск товара по ID: {}";

    // Warn level
    public static final String PRODUCT_ALREADY_DEACTIVATED = "Товар с ID {} уже был деактивирован";
    public static final String QUANTITY_STATE_UPDATE_FAILED = "Не удалось обновить количество товара с ID {}";
    public static final String HANDLER_PRODUCT_NOT_FOUND_EXCEPTION = "ProductNotFoundException exception: {}";
    public static final String HANDLER_ILLEGAL_ARGUMENT_EXCEPTION = "IllegalArgumentException exception: {}";
    public static final String HANDLER_METHOD_NOT_VALID_EXCEPTION = "MethodArgumentNotValidException exception: {}";
    public static final String HANDLER_CONSTRAINT_VIOLATION_EXCEPTION = "ConstraintViolationException exception: {}";
    public static final String HANDLER_NOT_AUTHORIZED_EXCEPTION = "NotAuthorizedException exception: {}";
    public static final String HANDLER_NO_PRODUCT_IN_CART = "NoProductsInShoppingCart exception: {}";
    public static final String HANDLE_PRODUCT_IN_WAREHOUSE_LOW = "ProductInShoppingCartLowQuantityInWarehouse exception: {}";
    public static final String HANDLE_FEIGN_EXCEPTION = "FeignException exception: {}";

    // Error level
    public static final String PRODUCT_NOT_ACTIVE = "Товар с ID {} неактивен";
    public static final String WAREHOUSE_SERVICE_ERROR = "Warehouse service недоступен. Error: {}";
    public static final String ERROR_CART_NOT_ACTIVE = "Нет активных корзин для пользователя: {}";
    public static final String ERROR_PRODUCT_NOT_FOUND_IN_CART = "Продукт не найден в корзине с id: {}";
    public static final String HANDLER_ERROR = "Unexpected error: {}";
    public static final String ERROR_WAREHOUSE_CHECK = "Ошибка при проверке склада: {}";
}
