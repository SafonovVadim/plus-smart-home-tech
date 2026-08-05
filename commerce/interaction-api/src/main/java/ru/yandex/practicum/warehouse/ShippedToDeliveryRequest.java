package ru.yandex.practicum.warehouse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ShippedToDeliveryRequest {
    private UUID deliveryId;

    private UUID orderId;
}
