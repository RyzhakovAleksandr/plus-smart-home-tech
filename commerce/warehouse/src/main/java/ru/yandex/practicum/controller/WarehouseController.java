package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PutMapping
    public void newProductInWarehouse(@Valid @RequestBody NewProductInWarehouseRequest request) {
        warehouseService.newProductInWarehouse(request);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductQuantityState(@Valid @RequestBody ShoppingCartDto shoppingCart) {
        return warehouseService.checkProductQuantityState(shoppingCart);
    }

    @PostMapping("/add")
    public void addProductToWarehouse(@Valid @RequestBody AddProductToWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}
