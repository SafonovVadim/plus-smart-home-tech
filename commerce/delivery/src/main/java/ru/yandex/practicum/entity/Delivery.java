package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.delivery.DeliveryState;
import ru.yandex.practicum.warehouse.AddressDto;

import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "from_address", columnDefinition = "jsonb")
    @Convert(converter = ru.yandex.practicum.converter.JsonbConverter.class)
    private AddressDto fromAddress;

    @Column(name = "to_address", columnDefinition = "jsonb")
    @Convert(converter = ru.yandex.practicum.converter.JsonbConverter.class)
    private AddressDto toAddress;

    @Column(name = "delivery_weight")
    private Double deliveryWeight;

    @Column(name = "delivery_volume")
    private Double deliveryVolume;

    @Column(name = "fragile")
    private Boolean fragile;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryState status;
}