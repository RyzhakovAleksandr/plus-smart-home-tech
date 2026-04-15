package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.model.CartProduct;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "shoppingCartId", source = "cartId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productsToMap")
    ShoppingCartDto toDto(Cart cart);

    @Named("productsToMap")
    default Map<UUID, Long> productsToMap(List<CartProduct> products) {
        if (products == null) {
            return Map.of();
        }
        return products.stream()
                .filter(cartItem -> cartItem.getProductId() != null && cartItem.getQuantity() != null)
                .collect(Collectors.toMap(
                        CartProduct::getProductId,
                        CartProduct::getQuantity,
                        Long::sum
                ));
    }
}
