package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.OrderState;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    @NotNull
    UUID orderId;

    UUID shoppingCartId;

    @NotNull
    @NotEmpty
    Map<UUID, @Positive Long> products;

    UUID paymentId;
    UUID deliveryId;
    OrderState state;
    Double deliveryWeight;
    Double deliveryVolume;
    Boolean fragile;
    Double totalPrice;
    Double deliveryPrice;
    Double productPrice;
}