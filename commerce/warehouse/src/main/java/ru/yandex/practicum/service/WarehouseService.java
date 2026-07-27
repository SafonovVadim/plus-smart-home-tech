package ru.yandex.practicum.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.shopping_cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.BookedProductsDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final String currentAddress;
    private final AddressDto addressDto;

    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
        this.currentAddress = ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];
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
        warehouseRepository.save(product);
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

}
