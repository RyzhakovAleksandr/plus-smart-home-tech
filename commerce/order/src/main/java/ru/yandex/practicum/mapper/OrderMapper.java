package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderProduct;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "products", source = "products", qualifiedByName = "productsToMap")
    OrderDto toDto(Order order);

    @Named("productsToMap")
    default Map<UUID, Long> productsToMap(List<OrderProduct> products) {
        if (products == null) {
            return Map.of();
        }
        return products.stream()
                .filter(product -> product.getProductId() != null && product.getQuantity() != null)
                .collect(Collectors.toMap(
                        OrderProduct::getProductId,
                        OrderProduct::getQuantity,
                        Long::sum
                ));
    }
}
