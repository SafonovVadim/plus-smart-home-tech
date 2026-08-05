package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.order.CreateNewOrderRequest;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderClient {
    private final OrderService orderService;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        return orderService.getClientOrders(username);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        return orderService.createNewOrder(request);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        return orderService.productReturn(request);
    }

    @Override
    public OrderDto payment(String orderId) {
        return orderService.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(String orderId) {
        return orderService.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(String orderId) {
        return orderService.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(String orderId) {
        return orderService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto complete(String orderId) {
        return orderService.complete(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(String orderId) {
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(String orderId) {
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(String orderId) {
        return orderService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(String orderId) {
        return orderService.assemblyFailed(orderId);
    }
}
