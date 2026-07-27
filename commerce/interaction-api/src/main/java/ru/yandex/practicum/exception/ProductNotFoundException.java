package ru.yandex.practicum.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message,String userMessage) {
        super(message);
    }
}
