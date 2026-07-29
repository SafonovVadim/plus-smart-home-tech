package ru.yandex.practicum.shopping_cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class ChangeProductQuantityRequest {
    @NotNull
    private UUID productId;
    @Positive
    private int newQuantity;
}
