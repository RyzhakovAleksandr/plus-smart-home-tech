package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.exceptions.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductDto> getProductByCategory(ProductCategory category, Pageable pageable) {
        log.debug(Message.FETCHING_PRODUCTS_BY_CATEGORY, category);
        Page<ProductDto> result = productRepository.findByProductCategory(category, pageable)
                .map(productMapper::toDto);
        log.info(Message.FOUND_PRODUCTS_COUNT,result.getTotalElements(), category);
        return result;
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        log.info(Message.CREATING_PRODUCT, productDto.getProductName());
        Product savedProduct = productRepository.save(productMapper.toEntity(productDto));
        log.info(Message.PRODUCT_CREATED_SUCCESS, savedProduct.getProductId(), savedProduct.getProductName());
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        UUID productId = productDto.getProductId();
        if (productId == null) {
            log.error(Message.PRODUCT_ID_MUST_BE);
            throw new IllegalArgumentException(Message.PRODUCT_ID_MUST_BE);
        }

        log.debug(Message.UPDATING_PRODUCT, productId);

        Product existingProduct = existProduct(productId);
        productMapper.updateEntity(productDto, existingProduct);

        Product updatedProduct = productRepository.save(existingProduct);
        log.info(Message.PRODUCT_UPDATED_SUCCESS, productId);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public boolean deactivateProduct(UUID productId) {
        log.info(Message.DEACTIVATING_PRODUCT, productId);
        validateProductActive(productId);

        int updatedRows = productRepository.deactivateProduct(productId);
        boolean success = updatedRows > 0;

        if (success) {
            log.info(Message.PRODUCT_DEACTIVATED_SUCCESS, productId);
        } else {
            log.warn(Message.PRODUCT_ALREADY_DEACTIVATED, productId);
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateQuantityState(SetProductQuantityStateRequest request) {
        UUID productId = request.getProductId();
        log.info(Message.UPDATING_QUANTITY_STATE, productId, request.getQuantityState());

        validateProductActive(request.getProductId());
        int updatedRows = productRepository.updateProductQuantity(productId, request.getQuantityState());
        boolean success = updatedRows > 0;

        if (success) {
            log.info(Message.QUANTITY_STATE_UPDATED_SUCCESS, productId, request.getQuantityState());
        } else {
            log.warn(Message.QUANTITY_STATE_UPDATE_FAILED, productId);
        }
        return success;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        log.debug(Message.FETCHING_PRODUCT_BY_ID, productId);
        return productMapper.toDto(existProduct(productId));
    }

    private Product existProduct(UUID productId) {
        return productRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format(Message.PRODUCT_NOT_FOUND, productId)));
    }

    private void validateProductActive(UUID productId) {
        if (!productRepository.existsByProductIdAndProductState(productId, ProductState.ACTIVE)) {
            log.error(Message.PRODUCT_NOT_ACTIVE, productId);
            throw new ProductNotFoundException(String.format(Message.PRODUCT_NOT_FOUND, productId));
        }
    }
}
