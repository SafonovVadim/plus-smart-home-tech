package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.entity.OrderMapper;
import ru.yandex.practicum.entity.OrderProduct;
import ru.yandex.practicum.feign.DeliveryClient;
import ru.yandex.practicum.feign.PaymentClient;
import ru.yandex.practicum.feign.ShoppingCartClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.order.CreateNewOrderRequest;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.OrderState;
import ru.yandex.practicum.order.ProductReturnRequest;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.BookedProductsDto;
import ru.yandex.practicum.warehouse.ReturnProductsRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.yandex.practicum.entity.OrderMapper.toDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseService;
    private final ShoppingCartClient shoppingCartClient;

    public List<OrderDto> getClientOrders(String username) {
        UUID shoppingCartId = shoppingCartClient.getCart(username).getShoppingCartId();
        return orderRepository.findByShoppingCartId(shoppingCartId)
                .map(List::of)
                .orElse(List.of())
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setShoppingCartId(request.getShoppingCart().getShoppingCartId());
        order.setState(OrderState.NEW);
        List<OrderProduct> orderProducts = request.getShoppingCart().getProducts().entrySet().stream()
                .map(entry -> OrderProduct.builder()
                        .id(UUID.randomUUID())
                        .productId(entry.getKey())
                        .quantity(entry.getValue())
                        .order(order)
                        .build())
                .collect(Collectors.toList());

        order.setProducts(orderProducts);
        orderRepository.save(order);
        log.info("Создан новый заказ: {}", order.getOrderId());
        return toDto(order);
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + request.getOrderId()));
        warehouseService.acceptReturn(ReturnProductsRequest.builder().returnProducts(request.getProducts()).build());

        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);

        log.info("Возврат заказа: {}", order.getOrderId());
        return toDto(order);
    }

    @Transactional
    public OrderDto payment(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));
        paymentClient.payment(toDto(order));

        order.setState(OrderState.PAID);
        orderRepository.save(order);

        log.info("Оплата заказа: {}", orderId);
        return toDto(order);
    }

    @Transactional
    public OrderDto paymentFailed(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        paymentClient.paymentFailed(UUID.fromString(orderId));

        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);

        log.info("Ошибка оплаты заказа: {}", orderId);
        return toDto(order);
    }

    @Transactional
    public OrderDto delivery(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        deliveryClient.deliverySuccessful(UUID.fromString(orderId));

        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);

        log.info("Доставка заказа: {}", orderId);
        return toDto(order);
    }

    @Transactional
    public OrderDto deliveryFailed(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        deliveryClient.deliveryFailed(UUID.fromString(orderId));

        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);

        log.info("Ошибка доставки заказа: {}", orderId);
        return toDto(order);
    }

    @Transactional
    public OrderDto complete(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);

        log.info("Заказ завершен: {}", orderId);
        return toDto(order);
    }

    @Transactional
    public OrderDto calculateTotalCost(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));
        OrderDto orderDto = toDto(order);
        Double totalCost = paymentClient.getTotalCost(orderDto);

        order.setTotalPrice(totalCost);
        orderRepository.save(order);

        log.info("Расчёт итоговой стоимости заказа: {}, total={}", orderId, totalCost);
        return toDto(order);
    }

    @Transactional
    public OrderDto calculateDeliveryCost(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        OrderDto orderDto = toDto(order);
        Double cost = deliveryClient.deliveryCost(orderDto);

        order.setDeliveryPrice(cost);
        orderRepository.save(order);

        log.info("Расчёт стоимости доставки заказа: {}, cost={}", orderId, cost);
        return toDto(order);
    }


    @Transactional
    public OrderDto assembly(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        Map<UUID, Long> products = order.getProducts().stream()
                .collect(Collectors.toMap(
                        OrderProduct::getProductId,
                        p -> p.getQuantity().longValue()
                ));

        BookedProductsDto booked = warehouseService.assemblyProductsForOrder(AssemblyProductsForOrderRequest.builder().orderId(UUID.fromString(orderId)).products(products).build());

        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.getFragile());
        order.setState(OrderState.ASSEMBLED);
        orderRepository.save(order);

        log.info("Сборка заказа: {}, вес={}, объем={}, хрупкий={}",
                orderId, booked.getDeliveryWeight(), booked.getDeliveryVolume(), booked.getFragile());
        return toDto(order);
    }

    @Transactional
    public OrderDto assemblyFailed(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        order.setState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);

        log.info("Ошибка сборки заказа: {}", orderId);
        return toDto(order);
    }
}
