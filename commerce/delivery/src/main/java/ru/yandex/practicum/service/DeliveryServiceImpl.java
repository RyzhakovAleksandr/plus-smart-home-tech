package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShipToDeliveryRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exceptions.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryRequest) {
        log.info(Message.PLANED_DELIVERY, deliveryRequest.getOrderId());

        Delivery newDelivery = deliveryMapper.toEntity(deliveryRequest);

        newDelivery.setDeliveryState(DeliveryState.CREATED);
        Delivery savedDelivery = deliveryRepository.save(newDelivery);

        log.info(Message.CREATED_DELIVERY, savedDelivery.getDeliveryId());
        return deliveryMapper.toDto(savedDelivery);
    }

    @Override
    @Transactional
    public void pickedDelivery(UUID orderId) {
        log.info(Message.CHECK_DELIVERY, orderId);

        Delivery delivery = getDelivery(orderId);

        if (delivery.getDeliveryState() != DeliveryState.CREATED) {
            log.warn(Message.DELIVERY_NOT_CREATED, delivery.getDeliveryState());
            throw new IllegalStateException(String.format(Message.DELIVERY_NOT_CREATED_EXCEPTION, delivery.getDeliveryState()));
        }

        warehouseClient.shippedToDelivery(ShipToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build());

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        log.info(Message.DELIVERY_STATE_IN_PROGRESS, delivery.getDeliveryId());
    }

    @Override
    @Transactional
    public void successfulDelivery(UUID orderId) {
        log.info(Message.DELIVERY_START_SUCCESS, orderId);

        Delivery delivery = getDelivery(orderId);

        if (delivery.getDeliveryState() != DeliveryState.IN_PROGRESS) {
            log.warn(Message.DELIVERY_STATE_NOT_IN_PROGRESS, delivery.getDeliveryState());
            throw new IllegalStateException(String.format(Message.DELIVERY_NOT_IN_PROGRESS_EXCEPTION, delivery.getDeliveryState()));
        }

        orderClient.deliverySuccess(orderId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        log.info(Message.INFO_DELIVERY_SUCCESS, delivery.getDeliveryId());
    }

    @Override
    @Transactional
    public void failedDelivery(UUID orderId) {
        log.info(Message.DELIVERY_FAILED, orderId);

        Delivery delivery = getDelivery(orderId);

        orderClient.deliveryFailed(orderId);

        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        log.info(Message.DELIVERY_STATE_FAILED, delivery.getDeliveryId());
    }

    @Override
    public Double deliveryCost(OrderDto orderDto) {
        log.info(Message.CALCULATE_TOTAL_DELIVERY, orderDto.getOrderId());

        Double weight = orderDto.getDeliveryWeight();
        Double volume = orderDto.getDeliveryVolume();
        Boolean fragile = orderDto.getFragile();

        Delivery delivery = getDelivery(orderDto.getOrderId());

        AddressDto warehouseAddress = deliveryMapper.toAddressDto(delivery.getFromAddress());
        AddressDto deliveryAddress = deliveryMapper.toAddressDto(delivery.getToAddress());

        // Базовая стоимость
        double cost = 5.0;
        // Умножаем базовую стоимость на число, зависящее от адреса склада
        if (warehouseAddress.getStreet().contains("ADDRESS_2")) {
            cost += cost * 2;
        }
        // Если в заказе есть признак хрупкости
        if (fragile) {
            cost += cost * 0.2;
        }
        // Вес заказа, умноженный на 0.3
        cost += weight * 0.3;
        // Объём, умноженный на 0.2
        cost += volume * 0.2;

        // Учёт адреса доставки
        if (!deliveryAddress.getStreet().equals(warehouseAddress.getStreet())) {
            cost += cost * 0.2;
        }

        log.info(Message.DELIVERY_TOTAL_SUM, orderDto.getOrderId(), cost);
        return cost;
    }

    private Delivery getDelivery(UUID orderId) {
        return deliveryRepository.findDeliveryByOrderId(orderId)
                .orElseThrow(() -> {
                    log.warn(Message.DELIVERY_NOT_FOUND, orderId);
                    return new NoDeliveryFoundException(String.format(Message.DELIVERY_NOT_FOUND_EXCEPTION, orderId));
                });
    }
}
