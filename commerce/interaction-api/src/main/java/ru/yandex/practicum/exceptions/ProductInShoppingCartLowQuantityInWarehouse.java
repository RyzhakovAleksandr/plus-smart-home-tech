package ru.yandex.practicum.exceptions;

public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException{
    public ProductInShoppingCartLowQuantityInWarehouse(String message) {
        super(message);
    }
}
