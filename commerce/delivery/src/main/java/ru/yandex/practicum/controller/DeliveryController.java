package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody UUID orderId) {
        deliveryService.successfulDelivery(orderId);
    }

    @PostMapping("/picked")
    public void pickedDelivery(@RequestBody UUID orderId) {
        deliveryService.pickedDelivery(orderId);
    }

    @PostMapping("/failed")
    public void failedDelivery(@RequestBody UUID orderId) {
        deliveryService.failedDelivery(orderId);
    }

    @PostMapping("/cost")
    public Double deliveryCost(@Valid @RequestBody OrderDto orderDto) {
        return deliveryService.deliveryCost(orderDto);
    }
}