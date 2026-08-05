package ru.yandex.practicum.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.shopping_cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.AddressDto;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateNewOrderRequest {

    @NotNull
    @Valid
    private ShoppingCartDto shoppingCart;

    @NotNull
    @Valid
    private AddressDto deliveryAddress;
}