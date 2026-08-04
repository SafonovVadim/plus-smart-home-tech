package ru.yandex.practicum.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.OrderBooking;
import ru.yandex.practicum.entity.WarehouseProduct;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.shopping_cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.*;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final AddressDto addressDto;
    private final OrderBookingRepository orderBookingRepository;
    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    public WarehouseService(WarehouseRepository warehouseRepository, OrderBookingRepository orderBookingRepository) {
        this.warehouseRepository = warehouseRepository;
        this.orderBookingRepository = orderBookingRepository;
        String currentAddress = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];
        this.addressDto = AddressDto.builder()
                .country(currentAddress)
                .city(currentAddress)
                .street(currentAddress)
                .house(currentAddress)
                .flat(currentAddress)
                .build();
        log.info("Адрес склада инициализирован: {}", currentAddress);
    }

    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.getProductId())) {
            throw new RuntimeException("Товар уже зарегистрирован на складе: " + request.getProductId());
        }

        WarehouseProduct product = WarehouseProduct.builder()
                .productId(request.getProductId())
                .quantity(0)
                .width(request.getDimension().getWidth())
                .height(request.getDimension().getHeight())
                .depth(request.getDimension().getDepth())
                .weight(request.getWeight())
                .fragile(request.getFragile())
                .build();

        warehouseRepository.save(product);
        log.info("Новый товар зарегистрирован на складе: productId={}", request.getProductId());
    }

    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = warehouseRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Товар не найден на складе: " + request.getProductId()));

        product.setQuantity(product.getQuantity() + request.getQuantity());
        log.info("Товар добавлен на склад: productId={}, added={}, total={}",
                request.getProductId(), request.getQuantity(), product.getQuantity());
    }

    @Transactional(readOnly = true)
    public AddressDto getAddress() {
        return addressDto;
    }

    @Transactional(readOnly = true)
    public BookedProductsDto checkAvailability(ShoppingCartDto cart) {
        Map<UUID, Integer> products = cart.getProducts();

        double totalWeight = 0;
        double totalVolume = 0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            WarehouseProduct warehouseProduct = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Товар не найден на складе: " + productId));

            if (warehouseProduct.getQuantity() < requestedQuantity) {
                throw new RuntimeException("Недостаточно товара на складе: productId=" + productId +
                        ", запрошено=" + requestedQuantity + ", доступно=" + warehouseProduct.getQuantity());
            }
            totalWeight += warehouseProduct.getWeight() * requestedQuantity;
            totalVolume += warehouseProduct.getWidth() * warehouseProduct.getHeight() * warehouseProduct.getDepth() * requestedQuantity;

            if (Boolean.TRUE.equals(warehouseProduct.getFragile())) {
                hasFragile = true;
            }
        }

        log.info("Проверка корзины {}: weight={}, volume={}, fragile={}",
                cart.getShoppingCartId(), totalWeight, totalVolume, hasFragile);

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrderRequest) {
        double totalWeight = 0;
        double totalVolume = 0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : assemblyProductsForOrderRequest.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();

            WarehouseProduct warehouseProduct = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Товар не найден: " + productId));

            if (warehouseProduct.getQuantity() < requestedQuantity) {
                throw new RuntimeException("Недостаточно товара: productId=" + productId +
                        ", запрошено=" + requestedQuantity + ", доступно=" + warehouseProduct.getQuantity());
            }

            warehouseProduct.setQuantity(warehouseProduct.getQuantity() - requestedQuantity.intValue());
            warehouseRepository.save(warehouseProduct);

            OrderBooking booking = OrderBooking.builder()
                    .bookingId(UUID.randomUUID())
                    .orderId(assemblyProductsForOrderRequest.getOrderId())
                    .productId(productId)
                    .quantity(requestedQuantity.intValue())
                    .build();
            orderBookingRepository.save(booking);

            totalWeight += warehouseProduct.getWeight() * requestedQuantity;
            totalVolume += warehouseProduct.getWidth() * warehouseProduct.getHeight() * warehouseProduct.getDepth() * requestedQuantity;
            if (Boolean.TRUE.equals(warehouseProduct.getFragile())) {
                hasFragile = true;
            }
        }

        log.info("Заказ {} собран: вес={}, объем={}, хрупкий={}", assemblyProductsForOrderRequest.getOrderId(), totalWeight, totalVolume, hasFragile);

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest shippedToDeliveryDto) {
        orderBookingRepository.findByOrderId(shippedToDeliveryDto.getOrderId())
                .forEach(booking -> {
                    booking.setDeliveryId(shippedToDeliveryDto.getDeliveryId());
                    orderBookingRepository.save(booking);
                });

        log.info("Заказ {} передан в доставку: deliveryId={}", shippedToDeliveryDto.getOrderId(), shippedToDeliveryDto.getDeliveryId());
    }

    @Transactional
    public void acceptReturn(ReturnProductsRequest returnProductsRequest) {
        for (Map.Entry<UUID, Long> entry : returnProductsRequest.getReturnProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            WarehouseProduct product = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Товар не найден: " + productId));

            product.setQuantity(product.getQuantity() + quantity.intValue());
            warehouseRepository.save(product);

            log.info("Товар возвращен на склад: productId={}, количество={}, всего={}",
                    productId, quantity, product.getQuantity());
        }
    }
}
