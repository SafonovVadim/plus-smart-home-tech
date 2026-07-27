package ru.yandex.practicum.exception;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    public NoSpecifiedProductInWarehouseException(String message, String userMessage) {
        super(message);
    }
}
