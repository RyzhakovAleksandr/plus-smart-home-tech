package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exceptions.NoProductsInShoppingCartException;
import ru.yandex.practicum.exceptions.NotAuthorizedUserException;
import ru.yandex.practicum.exceptions.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.messages.Message;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.model.CartProduct;
import ru.yandex.practicum.repository.CartProductRepository;
import ru.yandex.practicum.repository.CartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final CartMapper cartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.debug(Message.GETTING_CART, username);
        validateUsername(username);

        Cart cart = createCart(username);

        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> products) {
        log.info(Message.ADDING_PRODUCTS_TO_CART, username, products);
        validateUsername(username);

        Cart cart = createCart(username);
        Cart newCart = cartRepository.save(cart);
        ShoppingCartDto checkCart = cartMapper.toDto(newCart);

        Map<UUID, Long> allProducts = checkCart.getProducts();
        if (allProducts == null) {
            checkCart.setProducts(new HashMap<>());
        }

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            Long currentQuantity = allProducts.getOrDefault(entry.getKey(), 0L);
            allProducts.put(entry.getKey(), currentQuantity + entry.getValue());
        }
        checkCart.setProducts(allProducts);

        log.info(Message.WAREHOUSE_REQUEST, checkCart.getShoppingCartId(), checkCart.getProducts());

        try {
            warehouseClient.checkProductQuantityState(checkCart);
        } catch (Exception e) {
            log.error(Message.ERROR_WAREHOUSE_CHECK, e.getMessage());
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    String.format(Message.WAREHOUSE_CHECK_FAILED, e.getMessage()));
        }

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            addOrUpdateCartProduct(newCart, entry.getKey(), entry.getValue());
        }

        Cart savedCart = cartRepository.save(newCart);
        log.info(Message.PRODUCTS_ADDED_SUCCESS, savedCart.getCartId(), savedCart.getProducts().size());
        return cartMapper.toDto(savedCart);
    }

    @Override
    @Transactional
    public void deactivateCart(String username) {
        log.info(Message.DEACTIVATING_CART, username);
        validateUsername(username);

        int result = cartRepository.deactivateCart(username);
        if (result > 0) {
            log.info(Message.CART_DEACTIVATED_SUCCESS, username);
        } else {
            log.info(Message.CART_DEACTIVATED_NOT_FOUND, username);
        }
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String username, List<UUID> products) {
        log.info(Message.REMOVING_PRODUCTS_FROM_CART, username, products);
        validateUsername(username);

        Cart cart = findByUsernameIsActive(username);
        int result = cartProductRepository.deleteByCartIdAndProductIds(cart.getCartId(), products);

        log.info(Message.PRODUCTS_REMOVED_COUNT, result);

        cart = cartRepository.findByCartId(cart.getCartId()).orElse(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info(Message.CHANGING_PRODUCT_QUANTITY, username, request.getProductId(), request.getNewQuantity());
        validateUsername(username);

        Cart cart = findByUsernameIsActive(username);
        CartProduct cartProduct = cartProductRepository.findByCart_CartIdAndProductId(cart.getCartId(), request.getProductId())
                .orElseThrow(() -> {
                    log.error(Message.ERROR_PRODUCT_NOT_FOUND_IN_CART, request.getProductId());
                    return new NoProductsInShoppingCartException(
                            String.format(Message.PRODUCT_NOT_FOUND_IN_CART, request.getProductId()));
                });
        cartProduct.setQuantity(request.getNewQuantity());
        cartProductRepository.save(cartProduct);

        cart = cartRepository.findByCartId(cart.getCartId()).orElse(cart);
        log.info(Message.QUANTITY_CHANGED_SUCCESS);

        return cartMapper.toDto(cart);
    }

    private Cart createNewCart(String username) {
        log.info(Message.CART_CREATED, username);
        return Cart.builder()
                .username(username)
                .isActive(true)
                .build();
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.error(Message.USERNAME_MUST_BE);
            throw new NotAuthorizedUserException(Message.USERNAME_MUST_BE);
        }
    }

    private void addOrUpdateCartProduct(Cart cart, UUID productId, Long quantity) {
        cart.getProducts().stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(product -> product.setQuantity(product.getQuantity() + quantity),
                        () -> {
                            CartProduct newCartProduct = CartProduct.builder()
                                    .cart(cart)
                                    .productId(productId)
                                    .quantity(quantity)
                                    .build();
                            cart.getProducts().add(newCartProduct);
                        });
    }

    private Cart createCart(String username) {
        return cartRepository.findByUsername(username)
                .orElseGet(() -> {
                    log.info(Message.CART_NOT_EXISTS_CREATING, username);
                    return createNewCart(username);
                });
    }

    private Cart findByUsernameIsActive(String username) {
        return cartRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> {
                    log.error(Message.ERROR_CART_NOT_ACTIVE, username);
                    return new NoProductsInShoppingCartException(
                            String.format(Message.CART_NOT_ACTIVE, username));
                });
    }
}
