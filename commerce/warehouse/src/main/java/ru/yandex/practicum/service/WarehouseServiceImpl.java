package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.exceptions.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exceptions.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exceptions.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final ShoppingStoreClient shoppingStoreClient;

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        UUID productId = request.getProductId();
        log.info(Message.ADDING_NEW_PRODUCT_TO_WAREHOUSE, productId);

        if (warehouseRepository.existsByProductId(productId)) {
            log.error(Message.PRODUCT_ALREADY_IN_WAREHOUSE, productId);
            throw new SpecifiedProductAlreadyInWarehouseException(
                    String.format(Message.PRODUCT_ALREADY_IN_WAREHOUSE_EXCEPTION, productId));
        }

        WarehouseProduct entity = warehouseMapper.toEntity(request);
        warehouseRepository.save(entity);
        log.info(Message.PRODUCT_ADDED_TO_WAREHOUSE_SUCCESS, productId);
    }

    @Override
    @Transactional
    public BookedProductsDto checkProductQuantityState(ShoppingCartDto shoppingCartDto) {
        log.debug(Message.CHECKING_WAREHOUSE_AVAILABILITY, shoppingCartDto.getShoppingCartId());
        Map<UUID, Long> products = shoppingCartDto.getProducts();

        if (products == null || products.isEmpty()) {
            log.info(Message.WAREHOUSE_CHECK_EMPTY_CART);
            return BookedProductsDto.builder()
                    .deliveryWeight(0.0)
                    .deliveryVolume(0.0)
                    .fragile(false)
                    .build();
        }

        List<UUID> productIds = new ArrayList<>(products.keySet());
        Map<UUID, WarehouseProduct> warehouseProductMap = warehouseRepository.findByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(
                        WarehouseProduct::getProductId,
                        warehouseProduct -> warehouseProduct
                ));
        List<String> errors = new ArrayList<>();
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long requestQuantity = entry.getValue();

            WarehouseProduct product = warehouseProductMap.get(productId);

            if (product == null) {
                errors.add(String.format(Message.PRODUCT_NOT_FOUND_IN_WAREHOUSE_ERROR, productId));
                continue;
            }

            if (product.getQuantity() < requestQuantity) {
                errors.add(String.format(Message.PRODUCT_QUANTITY_INSUFFICIENT_ERROR,
                        productId, requestQuantity, product.getQuantity()));
            }

            totalWeight += (product.getWeight() != null ? product.getWeight() : 0.0) * requestQuantity;
            totalVolume += calculateVolume(product.getWidth(), product.getLength(), product.getHeight()) * requestQuantity;
            hasFragile = hasFragile || Boolean.TRUE.equals(product.getFragile());
        }
         if (!errors.isEmpty()) {
             String errorMessage = String.join("; ", errors);
             log.error(Message.WAREHOUSE_CHECK_FAILED_DETAILS, errorMessage);
             throw new ProductInShoppingCartLowQuantityInWarehouse(
                     String.format(Message.PRODUCT_QUANTITY_INSUFFICIENT, errorMessage));
         }
        log.info(Message.WAREHOUSE_CHECK_SUCCESS, totalWeight,  totalVolume, hasFragile);

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        UUID productId = request.getProductId();
        Long quantityToAdd = request.getQuantity();

        log.info(Message.ADDING_QUANTITY_TO_WAREHOUSE, productId, quantityToAdd);

        WarehouseProduct product = warehouseRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.error(Message.PRODUCT_NOT_FOUND_IN_WAREHOUSE, productId);
                    return new NoSpecifiedProductInWarehouseException(
                            String.format(Message.PRODUCT_NOT_FOUND_IN_WAREHOUSE_EXCEPTION, productId));
                });

        Long oldQuantity = product.getQuantity();
        product.setQuantity(oldQuantity + quantityToAdd);
        warehouseRepository.save(product);
        log.info(Message.QUANTITY_ADDED_TO_WAREHOUSE_SUCCESS, productId, oldQuantity, oldQuantity + quantityToAdd);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.debug(Message.GETTING_WAREHOUSE_ADDRESS, CURRENT_ADDRESS);
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    private Double calculateVolume(Double width, Double length, Double height) {
        if (width == null || length == null || height == null) {
            log.debug(Message.VOLUME_CALCULATION_MISSING_DIMENSIONS);
            return 0.0;
        }
        return width * length * height;
    }
}
