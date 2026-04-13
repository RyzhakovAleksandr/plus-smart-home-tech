package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {
    @PostMapping
    PaymentDto payment(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    Double getTotalCost(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/productCost")
    Double productsCost(@Valid @RequestBody OrderDto orderDto);
}
