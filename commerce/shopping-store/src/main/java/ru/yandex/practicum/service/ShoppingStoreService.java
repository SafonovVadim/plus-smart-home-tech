package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.entity.ProductMapper;
import ru.yandex.practicum.repository.ShoppingStoreRepository;
import ru.yandex.practicum.shopping_store.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.yandex.practicum.entity.ProductMapper.toDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingStoreService {

    private final ShoppingStoreRepository shoppingStoreRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(String category, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        Page<Product> products;
        if (category != null && !category.isEmpty()) {
            try {
                ProductCategory productCategory = ProductCategory.valueOf(category.toUpperCase());
                products = shoppingStoreRepository.findByProductStateAndProductCategory(ProductState.ACTIVE, productCategory, pageable);
            } catch (IllegalArgumentException e) {
                log.error("Неизвестная категория: {}", category);
                products = Page.empty();
            }
        } else {
            products = shoppingStoreRepository.findByProductState(ProductState.ACTIVE, pageable);
        }

        return products.map(ProductMapper::toDto);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "productName");
        }

        List<Sort.Order> orders = new ArrayList<>();
        String[] sortParams = sort.split(",");

        for (int i = 0; i < sortParams.length; i++) {
            String part = sortParams[i].trim();

            if (part.equalsIgnoreCase("ASC") || part.equalsIgnoreCase("DESC")) {
                continue;
            }

            Sort.Direction direction = Sort.Direction.ASC;
            if (i + 1 < sortParams.length) {
                String nextPart = sortParams[i + 1].trim();
                if (nextPart.equalsIgnoreCase("DESC")) {
                    direction = Sort.Direction.DESC;
                }
            }

            orders.add(new Sort.Order(direction, part));
        }

        if (orders.isEmpty()) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "productName"));
        }

        return Sort.by(orders);
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        Product product = shoppingStoreRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + productId));
        return toDto(product);
    }

    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = Product.builder()
                .productId(UUID.randomUUID())
                .productName(productDto.getProductName())
                .description(productDto.getDescription())
                .imageSrc(productDto.getImageSrc())
                .price(productDto.getPrice())
                .productCategory(productDto.getProductCategory())
                .quantityState(productDto.getQuantityState() != null ? productDto.getQuantityState() : QuantityState.MANY)
                .productState(productDto.getProductState() != null ? productDto.getProductState() : ProductState.ACTIVE)
                .build();
        product = shoppingStoreRepository.save(product);
        log.info("Добавлен новый товар: {}", product.getProductId());
        return toDto(product);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = shoppingStoreRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + productDto.getProductId()));

        if (productDto.getProductName() != null) product.setProductName(productDto.getProductName());
        if (productDto.getDescription() != null) product.setDescription(productDto.getDescription());
        if (productDto.getImageSrc() != null) product.setImageSrc(productDto.getImageSrc());
        if (productDto.getPrice() != null) product.setPrice(productDto.getPrice());
        if (productDto.getProductCategory() != null) product.setProductCategory(productDto.getProductCategory());
        if (productDto.getQuantityState() != null) product.setQuantityState(productDto.getQuantityState());
        log.info("Обновлён товар: {}", product.getProductId());
        return toDto(product);
    }

    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        Product product = shoppingStoreRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + productId));

        product.setProductState(ProductState.DEACTIVATE);
        log.info("Товар деактивирован: {}", productId);
        return true;
    }

    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = shoppingStoreRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + request.getProductId()));

        product.setQuantityState(request.getQuantityState());
        log.info("Изменено количество товара {}: {}", request.getProductId(), request.getQuantityState());
        return true;
    }
}
