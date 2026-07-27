package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.service.ShoppingStoreService;
import ru.yandex.practicum.shopping_cart.ChangeProductQuantityRequest;

import ru.yandex.practicum.shopping_store.ProductDto;
import ru.yandex.practicum.shopping_store.SetProductQuantityStateRequest;

import java.util.UUID;

@RestController()
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {
    private final ShoppingStoreService shoppingStoreService;

    @Override
    public Page<ProductDto> getProductByCategory(String category, int page, int size, String sort) {
        return shoppingStoreService.getProducts(category, page, size, sort);
    }

    @Override
    public ProductDto addNewProduct(ProductDto product) {
        return shoppingStoreService.createNewProduct(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto product) {
        return shoppingStoreService.updateProduct(product);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        return shoppingStoreService.removeProductFromStore(productId);
    }

    @Override
    public boolean changeState(SetProductQuantityStateRequest request) {
        return shoppingStoreService.setProductQuantityState(request);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return shoppingStoreService.getProduct(productId);
    }
}
