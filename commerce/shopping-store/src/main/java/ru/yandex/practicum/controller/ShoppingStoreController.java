package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {
    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    public Page<ProductDto> getProducts(
            @RequestParam("category") ProductCategory category,
            @PageableDefault(page = 0, size = 20, sort = "productName") Pageable pageable) {
        return shoppingStoreService.getProductByCategory(category, pageable);
    }

    @PutMapping
    public ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto) {
        return shoppingStoreService.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        return shoppingStoreService.deactivateProduct(productId);
    }

    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestParam UUID productId,
                                           @RequestParam QuantityState quantityState) {
        SetProductQuantityStateRequest request = SetProductQuantityStateRequest.builder()
                .productId(productId)
                .quantityState(quantityState)
                .build();
        return shoppingStoreService.updateQuantityState(request);
    }

    @GetMapping("/{product_id}")
    public ProductDto getProductById(@PathVariable("product_id") UUID productId) {
        return shoppingStoreService.getProductById(productId);
    }
}
