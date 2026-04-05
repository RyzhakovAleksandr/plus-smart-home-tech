package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product entity);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductDto dto);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ProductDto dto, @MappingTarget Product entity);
}
