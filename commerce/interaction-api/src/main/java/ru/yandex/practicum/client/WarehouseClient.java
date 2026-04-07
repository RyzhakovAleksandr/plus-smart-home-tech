package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.ShoppingCartResponse;
import ru.yandex.practicum.dto.WarehouseCheckResponse;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {
    @PostMapping("/check")
    WarehouseCheckResponse checkProductAvailability(@Valid @RequestBody ShoppingCartResponse shoppingCartResponse);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
