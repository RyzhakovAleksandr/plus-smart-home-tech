package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartResponse getShoppingCart(String username);

    ShoppingCartResponse addProducts(String username, Map<UUID, Long> products);

    void deactivateCart(String username);

    ShoppingCartResponse removeProducts(String username, List<UUID> products);

    ShoppingCartResponse changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
