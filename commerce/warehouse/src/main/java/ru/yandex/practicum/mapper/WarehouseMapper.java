package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantity", constant = "0L")
    @Mapping(target = "width", source = "dimension.width")
    @Mapping(target = "length", source = "dimension.length")
    @Mapping(target = "height", source = "dimension.height")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WarehouseProduct toEntity(NewProductInWarehouseRequest request);
}
