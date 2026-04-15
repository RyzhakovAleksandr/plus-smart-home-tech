package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto payment(OrderDto orderDto);

    Double getTotalCost(OrderDto orderDto);

    void paymentSuccess(UUID paymentId);

    Double productCost(OrderDto orderDto);

    void paymentFailed(UUID paymentId);
}
