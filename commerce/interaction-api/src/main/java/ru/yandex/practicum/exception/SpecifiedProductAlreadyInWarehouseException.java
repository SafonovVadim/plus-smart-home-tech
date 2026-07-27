package ru.yandex.practicum.exception;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    public SpecifiedProductAlreadyInWarehouseException(String message,String userMessage) {
        super(message);
    }
}
