package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.CreateNewOrderRequest;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.ProductReturnRequest;

import java.util.List;

@FeignClient(name = "order", path = "/api/v1/order", configuration = FeignConfig.class)
public interface OrderClient {

    @GetMapping
    List<OrderDto> getClientOrders(@RequestParam String username);

    @PutMapping
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto productReturn(@ModelAttribute ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto payment(@RequestBody String orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody String orderId);

    @PostMapping("/delivery")
    OrderDto delivery(@RequestBody String orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody String orderId);

    @PostMapping("/completed")
    OrderDto complete(@RequestBody String orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody String orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody String orderId);

    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody String orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody String orderId);
}