package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.feign.DeliveryClient;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.OrderState;
import ru.yandex.practicum.payment.PaymentDto;
import ru.yandex.practicum.payment.PaymentStatus;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final DeliveryClient deliveryClient;

    @Value("${payment.tax-rate:10}")
    private double taxRate;

    public PaymentDto payment(OrderDto orderDto) {
        Double productCost = productCost(orderDto);
        Double deliveryCost = deliveryClient.deliveryCost(orderDto);
        Double totalCost = calculateTotalCost(productCost, deliveryCost);

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .orderId(orderDto.getOrderId())
                .productCost(productCost)
                .deliveryCost(deliveryCost)
                .totalCost(totalCost)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        log.info("Оплата создана: {}", payment.getPaymentId());

        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .totalPayment(payment.getTotalCost())
                .deliveryTotal(payment.getDeliveryCost())
                .feeTotal(productCost * taxRate / 100)
                .build();
    }

    public Double getTotalCost(OrderDto orderDto) {
        Double productCost = productCost(orderDto);
        Double deliveryCost = deliveryClient.deliveryCost(orderDto);
        return calculateTotalCost(productCost, deliveryCost);
    }

    public void paymentSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Оплата не найдена: " + paymentId));

        payment.setStatus(PaymentStatus.SUCCEEDED);
        paymentRepository.save(payment);

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + payment.getOrderId()));
        order.setState(OrderState.PAID);
        orderRepository.save(order);

        log.info("Оплата прошла успешно: {}, статус заказа обновлен на PAID", paymentId);
    }

    public Double productCost(OrderDto orderDto) {
        Map<UUID, Long> products = orderDto.getProducts();
        double total = 0.0;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            Double price = shoppingStoreClient.getProduct(productId).getPrice();
            total += price * quantity;
        }

        return total;
    }

    public void paymentFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Оплатана не найдена: " + paymentId));

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + payment.getOrderId()));
        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);

        log.info("Оплата не прошла: {}, статус заказа обновлен на PAYMENT_FAILED", paymentId);
    }

    private Double calculateTotalCost(Double productCost, Double deliveryCost) {
        Double tax = productCost * taxRate / 100;
        return productCost + tax + deliveryCost;
    }
}