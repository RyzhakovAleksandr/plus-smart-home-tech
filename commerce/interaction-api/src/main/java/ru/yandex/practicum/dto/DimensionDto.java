package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @NotNull
    @DecimalMin("1.0")
    Double width;

    @NotNull
    @DecimalMin("1.0")
    Double length;

    @NotNull
    @DecimalMin("1.0")
    Double height;
}
