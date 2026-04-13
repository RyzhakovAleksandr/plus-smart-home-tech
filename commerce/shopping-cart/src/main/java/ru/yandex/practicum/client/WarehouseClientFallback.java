package ru.yandex.practicum.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ShoppingCartResponse;
import ru.yandex.practicum.dto.WarehouseCheckResponse;
import ru.yandex.practicum.messages.Message;

@Slf4j
@Component
@RequiredArgsConstructor
public class WarehouseClientFallback {
    private final WarehouseClient warehouseClient;

    @CircuitBreaker(name = "warehouse", fallbackMethod = "checkProductQuantityFallback")
    public WarehouseCheckResponse checkProductQuantityState(ShoppingCartResponse shoppingCartResponse) {
        return warehouseClient.checkProductAvailability(shoppingCartResponse);
    }

    private WarehouseCheckResponse checkProductQuantityFallback(ShoppingCartResponse shoppingCartResponse, Throwable throwable) {
        log.error(Message.WAREHOUSE_SERVICE_ERROR, throwable.getMessage());
        throw new RuntimeException(Message.SERVER_UNAVAILABLE);
    }
}
