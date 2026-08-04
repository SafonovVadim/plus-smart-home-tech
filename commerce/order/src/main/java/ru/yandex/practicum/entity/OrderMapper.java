package ru.yandex.practicum.entity;

import ru.yandex.practicum.order.OrderDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMapper {

    public static Order toEntity(OrderDto dto) {
        List<OrderProduct> products = dto.getProducts().entrySet().stream()
                .map(entry -> OrderProduct.builder()
                        .id(UUID.randomUUID())
                        .productId(entry.getKey())
                        .quantity(entry.getValue().intValue())
                        .build())
                .collect(Collectors.toList());
        Order order = Order.builder()
                .orderId(dto.getOrderId())
                .shoppingCartId(dto.getShoppingCartId())
                .paymentId(dto.getPaymentId())
                .deliveryId(dto.getDeliveryId())
                .state(dto.getState())
                .deliveryWeight(dto.getDeliveryWeight())
                .deliveryVolume(dto.getDeliveryVolume())
                .fragile(dto.getFragile())
                .totalPrice(dto.getTotalPrice())
                .deliveryPrice(dto.getDeliveryPrice())
                .productPrice(dto.getProductPrice())
                .products(products)
                .build();
        products.forEach(p -> p.setOrder(order));

        return order;
    }

    public static OrderDto toDto(Order order) {
        Map<UUID, Long> products = order.getProducts().stream()
                .collect(Collectors.toMap(
                        OrderProduct::getProductId,
                        p -> p.getQuantity().longValue()
                ));

        return OrderDto.builder()
                .orderId(order.getOrderId())
                .shoppingCartId(order.getShoppingCartId())
                .products(products)
                .paymentId(order.getPaymentId())
                .deliveryId(order.getDeliveryId())
                .state(order.getState())
                .deliveryWeight(order.getDeliveryWeight())
                .deliveryVolume(order.getDeliveryVolume())
                .fragile(order.getFragile())
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .build();
    }
}
