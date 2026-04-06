package ru.yandex.practicum.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.messages.Message;

@Slf4j
@Component
@RequiredArgsConstructor
public class WarehouseClientFallback {
    private final WarehouseClient warehouseClient;

    @CircuitBreaker(name = "warehouse", fallbackMethod = "checkProductQuantityFallback")
    public BookedProductsDto checkProductQuantityState(ShoppingCartDto shoppingCartDto) {
        return warehouseClient.checkProductQuantityState(shoppingCartDto);
    }

    private BookedProductsDto checkProductQuantityFallback(ShoppingCartDto shoppingCartDto, Throwable throwable) {
        log.error(Message.WAREHOUSE_SERVICE_ERROR, throwable.getMessage());
        throw new RuntimeException(Message.SERVER_UNAVAILABLE);
    }
}
