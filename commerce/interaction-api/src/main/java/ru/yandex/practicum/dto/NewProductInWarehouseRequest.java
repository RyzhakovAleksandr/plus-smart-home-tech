package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {
    @NotNull
    UUID productId;

    Boolean fragile;

    @NotNull
    DimensionDto dimension;

    @NotNull
    @DecimalMin("1.0")
    Double weight;
}
