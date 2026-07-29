package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shopping_cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.shopping_cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart",configuration = FeignConfig.class)
public interface ShoppingCartClient {

    @GetMapping()
    ShoppingCartDto getCart(@RequestParam String username);

    @PutMapping()
    ShoppingCartDto putProductInCart(@RequestParam String username, @RequestBody Map<UUID, Integer> products);

    @DeleteMapping()
    void deactivateCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProductFromCart(@RequestParam String username, @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantityInCart(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request);
}
