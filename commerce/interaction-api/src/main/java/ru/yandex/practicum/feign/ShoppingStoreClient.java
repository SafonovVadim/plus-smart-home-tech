package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shopping_store.ProductDto;
import ru.yandex.practicum.shopping_store.SetProductQuantityStateRequest;

import java.util.UUID;

@FeignClient(name = "shopping-store",path = "/api/v1/shopping-store",configuration = FeignConfig.class)
public interface ShoppingStoreClient {

    @GetMapping()
    Page<ProductDto> getProductByCategory(@RequestParam String category,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size,
                              @RequestParam(required = false) String sort);

    @PutMapping()
    ProductDto addNewProduct(@RequestBody ProductDto product);

    @PostMapping()
    ProductDto updateProduct(@RequestBody ProductDto product);

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestParam UUID productId);

    @PostMapping("/quantityState")
    boolean changeState(@RequestBody SetProductQuantityStateRequest request);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
