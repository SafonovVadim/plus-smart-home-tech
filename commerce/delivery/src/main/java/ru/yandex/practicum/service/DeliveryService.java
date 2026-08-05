package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.delivery.DeliveryState;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.OrderState;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.ShippedToDeliveryRequest;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;

    @Value("${delivery.base-cost:5.0}")
    private double baseCost;

    @Value("${delivery.address1.multiplier:1.0}")
    private double address1Multiplier;

    @Value("${delivery.address2.multiplier:2.0}")
    private double address2Multiplier;

    @Value("${delivery.fragile.multiplier:0.2}")
    private double fragileMultiplier;

    @Value("${delivery.weight.cost:0.3}")
    private double weightCost;

    @Value("${delivery.volume.cost:0.2}")
    private double volumeCost;

    @Value("${delivery.street-mismatch.multiplier:0.2}")
    private double streetMismatchMultiplier;

    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Order order = orderRepository.findById(deliveryDto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + deliveryDto.getOrderId()));

        Delivery delivery = Delivery.builder()
                .deliveryId(UUID.randomUUID())
                .orderId(deliveryDto.getOrderId())
                .deliveryWeight(order.getDeliveryWeight())
                .deliveryVolume(order.getDeliveryVolume())
                .fragile(order.getFragile())
                .fromAddress(deliveryDto.getFromAddress())
                .toAddress(deliveryDto.getToAddress())
                .status(DeliveryState.CREATED)
                .build();

        deliveryRepository.save(delivery);
        log.info("Доставка создана: {}", delivery.getDeliveryId());
        return deliveryDto;
    }

    public void deliveryPicked(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Доставка не найдена для заказа: " + orderId));

        delivery.setStatus(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));
        order.setState(OrderState.ASSEMBLED);
        orderRepository.save(order);

        warehouseClient.shippedToDelivery(ShippedToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build());
        log.info("Доставка принята в доставку: {}, статус заказа обновлен на ASSEMBLED", delivery.getDeliveryId());
    }

    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Доставка не найдена для заказа: " + orderId));

        delivery.setStatus(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));
        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);

        log.info("Доставка успешна: {}, статус заказа обновлен на DELIVERED", delivery.getDeliveryId());
    }

    public void deliveryFailed(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Доставка не найдена для заказа: " + orderId));

        delivery.setStatus(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));
        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);

        log.info("Доставка не удалась: {}, статус заказа обновлен на DELIVERY_FAILED", delivery.getDeliveryId());
    }

    public Double deliveryCost(OrderDto orderDto) {
        AddressDto fromAddress = warehouseClient.getAddress();
        AddressDto toAddress = deliveryRepository.findByOrderId(orderDto.getOrderId()).orElseThrow(() -> new RuntimeException("Доставка не найдена для заказа: " + orderDto.getOrderId())).getToAddress();

        double current = baseCost;
        String fromAddressFull = fromAddress.getStreet() + " " + fromAddress.getCity();

        if (fromAddressFull.contains("ADDRESS_1")) {
            current = current + (current * address1Multiplier);
        } else if (fromAddressFull.contains("ADDRESS_2")) {
            current = current + (current * address2Multiplier);
        }

        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            double fragileAdd = current * fragileMultiplier;
            current += fragileAdd;
        }

        double weight = orderDto.getDeliveryWeight() != null ? orderDto.getDeliveryWeight() : 0.0;
        double volume = orderDto.getDeliveryVolume() != null ? orderDto.getDeliveryVolume() : 0.0;

        current += weight * weightCost;
        current += volume * volumeCost;

        boolean streetMatch = fromAddress.getStreet().equals(toAddress.getStreet());
        if (!streetMatch) {
            double streetAdd = current * streetMismatchMultiplier;
            current += streetAdd;
        }

        log.info("Расчет стоимости доставки для заказа {}: {}", orderDto.getOrderId(), current);
        return current;
    }
}