package ru.yandex.practicum.exception;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException(String message,String userMessage) {
        super(message);
    }
}
