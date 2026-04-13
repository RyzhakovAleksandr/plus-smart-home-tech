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
    public static final String PRODUCT_ALREADY_IN_WAREHOUSE_EXCEPTION = "Продукт уже есть на складе: %s";
    public static final String PRODUCT_NOT_FOUND_IN_WAREHOUSE_EXCEPTION = "Товар для добавления не найден: %s";
    public static final String PRODUCT_NOT_FOUND_IN_WAREHOUSE_ERROR = "Товар не найден на складе: %s";
    public static final String PRODUCT_QUANTITY_INSUFFICIENT_ERROR = "Товар id: %s, запрошено: %d, в наличии: %d";
    public static final String PRODUCT_QUANTITY_INSUFFICIENT = "Недостаточное количество товара на складе: %s";
    public static final String NO_CORRECT_USERNAME_EXCEPTION = "Некорректное имя пользователя";
    public static final String ORDER_NOT_FOUND_EXCEPTION = "Заказ не найден: %s";
    public static final String ORDER_NOT_ASSEMBLED_EXCEPTION = "Заказ не находится в статусе ASSEMBLED. Текущий статус: %s";
    public static final String ORDER_NO_PAYMENT_EXCEPTION = "Заказ не находится в статусе ON_PAYMENT. Текущий статус: %s";
    public static final String ORDER_NOT_DELIVERY_STATE_EXCEPTION = "Заказ не находится в статусе ON_DELIVERY. Текущий статус: %s";
    public static final String DELIVERY_NO_POSSIBLE_EXCEPTION = "Доставка невозможна. Заказ находиться в статусе: %s";
    public static final String ORDER_NO_CORRECT_STATE_EXCEPTION = "Заказ не может быть завершен в статусе %s";
    public static final String ORDER_CANT_ASSEMBLY_STATE_EXCEPTION = "Заказ не может быть собран в состоянии: %s";

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
    public static final String ADDING_NEW_PRODUCT_TO_WAREHOUSE = "Добавление нового товара на склад: {}";
    public static final String PRODUCT_ADDED_TO_WAREHOUSE_SUCCESS = "Товар успешно добавлен на склад: {}";
    public static final String ADDING_QUANTITY_TO_WAREHOUSE = "Добавление количества товару {}: +{} единиц";
    public static final String QUANTITY_ADDED_TO_WAREHOUSE_SUCCESS = "Количество товара {} обновлено: {} → {}";
    public static final String WAREHOUSE_CHECK_EMPTY_CART = "Проверка склада: корзина пуста";
    public static final String WAREHOUSE_CHECK_SUCCESS = "Проверка склада пройдена. Вес: {}, Объем: {}, Хрупкий: {}";
    public static final String GETTING_WAREHOUSE_ADDRESS = "Получение адреса склада: {}";
    public static final String GETTING_ORDER = "Получение заказов для пользователя: {}";
    public static final String CREATE_NEW_ORDER = "Создание нового заказа для пользователя: {}";
    public static final String CREATED_ORDERS_OK = "Заказ создан с идентификатором: {}";
    public static final String WORK_PAYMENT_ORDER = "Обработка платежа по заказу:  {}";
    public static final String INITIAL_PAYMENT = "Инициирован платеж по заказу: {}";
    public static final String RETURN_ORDER = "Оформление возврата заказа: {}";
    public static final String CHANGE_ORDER = "Статус заказа {} изменен на {}";
    public static final String MESSAGE_PAYMENT_OK = "Получено уведомление об успешной оплате заказа: {}";
    public static final String MESSAGE_PAYMENT_FAULT = "Получено уведомление о неудачной оплате заказа: {}";
    public static final String ORDER_GO_DELIVERY = "Передача заказа {} в службу доставки";
    public static final String INFO_DELIVERY_SUCCESS = "Получено уведомление об удачной доставке заказа: {}";
    public static final String INFO_DELIVERY_FAILED = "Получено уведомление о неудачной доставке заказа: {}";
    public static final String DELIVERY_SUCCESS = "Заказ {} успешно доставлен";
    public static final String DELIVERY_COMPLETE = "Завершение заказа: {}";
    public static final String DELIVERY_COMPLETE_OK = "Заказ успешно завершен: {}";
    public static final String CALCULATE_TOTAL_SUM = "Запрос для получения итоговой суммы заказа: {}";
    public static final String CALCULATE_TOTAL_DELIVERY = "Запрос на получения стоимости доставки для заказа: {}";
    public static final String ASSEMBLY_ORDER = "Сборка заказа: {}";
    public static final String PRODUCT_ASSEMBLED = "Товар забронирован для заказа: {}";
    public static final String PRODUCT_ADDED_TO_ORDER = "Заказ {} успешно собран";
    public static final String PRODUCT_ADDED_TO_ORDER_FAULT = "Сборка заказа {} не удалась";

    // Debug level
    public static final String GETTING_CART = "Получение корзины для пользователя: {}";
    public static final String FETCHING_PRODUCTS_BY_CATEGORY = "Запрос товаров по категории: {}";
    public static final String FETCHING_PRODUCT_BY_ID = "Поиск товара по ID: {}";
    public static final String CHECKING_WAREHOUSE_AVAILABILITY = "Проверка наличия товаров на складе для корзины: {}";
    public static final String VOLUME_CALCULATION_MISSING_DIMENSIONS = "Отсутствуют размеры товара, объем = 0";

    // Warn level
    public static final String PRODUCT_ALREADY_DEACTIVATED = "Товар с ID {} уже был деактивирован";
    public static final String QUANTITY_STATE_UPDATE_FAILED = "Не удалось обновить количество товара с ID {}";
    public static final String HANDLER_PRODUCT_NOT_FOUND_EXCEPTION = "ProductNotFoundException exception: {}";
    public static final String HANDLER_ILLEGAL_ARGUMENT_EXCEPTION = "IllegalArgumentException exception: {}";
    public static final String HANDLER_ILLEGAL_STATE_EXCEPTION = "IllegalStateException error: {}";
    public static final String HANDLER_METHOD_NOT_VALID_EXCEPTION = "MethodArgumentNotValidException exception: {}";
    public static final String HANDLER_CONSTRAINT_VIOLATION_EXCEPTION = "ConstraintViolationException exception: {}";
    public static final String HANDLER_NOT_AUTHORIZED_EXCEPTION = "NotAuthorizedException exception: {}";
    public static final String HANDLER_NO_PRODUCT_IN_CART = "NoProductsInShoppingCart exception: {}";
    public static final String HANDLER_NO_PRODUCT_IN_WAREHOUSE = "NoSpecifiedProductInWarehouseException exception: {}";
    public static final String HANDLER_PRODUCT_IN_WAREHOUSE_LOW = "ProductInShoppingCartLowQuantityInWarehouse exception: {}";
    public static final String HANDLER_FEIGN_EXCEPTION = "FeignException exception: {}";
    public static final String HANDLER_ALREADY_IN_WAREHOUSE = "SpecifiedProductAlreadyInWarehouseException exception: {}";
    public static final String HANDLER_NO_ORDER_FOUND = "NoOrderFoundException error: {}";
    public static final String NO_CORRECT_USERNAME = "Некорректное имя пользователя: {}";
    public static final String ORDER_NOT_FOUND = "Заказ не найден: {}";
    public static final String ORDER_NOT_ASSEMBLED_STATE = "Заказ {} не находится в статусе ASSEMBLED. Текущий статус: {}";
    public static final String ORDER_NOT_ON_PAYMENT_STATE = "Заказ {} не находится в статусе ON_PAYMENT. Текущий статус: {}";
    public static final String ORDER_NOT_DELIVERY_STATE = "Заказ {} не находится в статусе ON_DELIVERY. Текущий статус: {}";
    public static final String DELIVERY_NO_POSSIBLE = "Доставка невозможна. Заказ находиться в статусе: {}";
    public static final String ORDER_NO_CORRECT_STATE = "Заказ {} не может быть завершен в статусе {}";
    public static final String ORDER_CANT_ASSEMBLY_STATE = "Заказ не может быть собран в состоянии: {}";

    // Error level
    public static final String PRODUCT_NOT_ACTIVE = "Товар с ID {} неактивен";
    public static final String WAREHOUSE_SERVICE_ERROR = "Warehouse service недоступен. Error: {}";
    public static final String ERROR_CART_NOT_ACTIVE = "Нет активных корзин для пользователя: {}";
    public static final String ERROR_PRODUCT_NOT_FOUND_IN_CART = "Продукт не найден в корзине с id: {}";
    public static final String HANDLER_ERROR = "Unexpected error: {}";
    public static final String ERROR_WAREHOUSE_CHECK = "Ошибка при проверке склада: {}";
    public static final String PRODUCT_ALREADY_IN_WAREHOUSE = "Товар уже есть на складе: {}";
    public static final String PRODUCT_NOT_FOUND_IN_WAREHOUSE = "Товар не найден на складе: {}";
    public static final String WAREHOUSE_CHECK_FAILED_DETAILS = "Ошибка проверки склада: {}";
}
