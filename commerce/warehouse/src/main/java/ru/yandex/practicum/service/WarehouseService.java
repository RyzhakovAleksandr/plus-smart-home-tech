package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

public interface WarehouseService {
    void newProductInWarehouse(@Valid NewProductInWarehouseRequest request);

    BookedProductsDto checkProductQuantityState(@Valid ShoppingCartDto shoppingCartDto);

    void addProductToWarehouse(@Valid AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}
