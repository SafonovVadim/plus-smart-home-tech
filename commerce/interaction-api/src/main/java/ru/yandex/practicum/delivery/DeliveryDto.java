package ru.yandex.practicum.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.warehouse.AddressDto;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeliveryDto {

    private UUID deliveryId;

    @NotNull
    @Valid
    private AddressDto fromAddress;

    @NotNull
    @Valid
    private AddressDto toAddress;

    @NotNull
    private UUID orderId;

    @NotNull
    private DeliveryState deliveryState;
}