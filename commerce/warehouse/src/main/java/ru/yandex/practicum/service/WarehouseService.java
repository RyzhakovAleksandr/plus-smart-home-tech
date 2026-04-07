package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.WarehouseCheckResponse;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartResponse;

public interface WarehouseService {
    void newProductInWarehouse(@Valid NewProductInWarehouseRequest request);

    WarehouseCheckResponse checkProductQuantityState(@Valid ShoppingCartResponse shoppingCartResponse);

    void addProductToWarehouse(@Valid AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}
