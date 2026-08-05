package ru.yandex.practicum.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.shopping_cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.*;

@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseClient {

    private static final String WAREHOUSE_UNAVAILABLE_MESSAGE = "Сервис склада временно недоступен. Попробуйте позже.";
    private static final String WAREHOUSE_UNAVAILABLE_ADDRESS = "Адрес доставки временно недоступен.";

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        log.warn("WarehouseClient fallback: addNewProduct called. {}", WAREHOUSE_UNAVAILABLE_MESSAGE);
        throw new RuntimeException(WAREHOUSE_UNAVAILABLE_MESSAGE);
    }

    @Override
    public BookedProductsDto checkProduct(ShoppingCartDto shoppingCartDto) {
        log.warn("WarehouseClient fallback: checkProduct called. {}", WAREHOUSE_UNAVAILABLE_MESSAGE);
        throw new RuntimeException(WAREHOUSE_UNAVAILABLE_MESSAGE);
    }

    @Override
    public void addProduct(AddProductToWarehouseRequest request) {
        log.warn("WarehouseClient fallback: addProduct called. {}", WAREHOUSE_UNAVAILABLE_MESSAGE);
        throw new RuntimeException(WAREHOUSE_UNAVAILABLE_MESSAGE);
    }

    @Override
    public AddressDto getAddress() {
        log.warn("WarehouseClient fallback: getAddress called. {}", WAREHOUSE_UNAVAILABLE_ADDRESS);
        throw new RuntimeException(WAREHOUSE_UNAVAILABLE_ADDRESS);
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.warn("WarehouseClient fallback: shippedToDelivery called. {}", WAREHOUSE_UNAVAILABLE_ADDRESS);
        throw new RuntimeException(WAREHOUSE_UNAVAILABLE_ADDRESS);
    }

    @Override
    public void acceptReturn(ReturnProductsRequest request) {
        log.warn("WarehouseClient fallback: acceptReturn called. {}", WAREHOUSE_UNAVAILABLE_ADDRESS);
        throw new RuntimeException(WAREHOUSE_UNAVAILABLE_ADDRESS);
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        log.warn("WarehouseClient fallback: assemblyProductsForOrder called. {}", WAREHOUSE_UNAVAILABLE_ADDRESS);
        throw new RuntimeException(WAREHOUSE_UNAVAILABLE_ADDRESS);
    }
}
