package ru.yandex.practicum.exception;

public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    public ProductInShoppingCartLowQuantityInWarehouse(String message,String userMessage) {
        super(message);
    }
}
