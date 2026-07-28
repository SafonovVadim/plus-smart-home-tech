package ru.yandex.practicum.entity;

public class ProductMapper {

    public static ru.yandex.practicum.shopping_store.ProductDto toDto(Product product) {
        return ru.yandex.practicum.shopping_store.ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .productState(product.getProductState())
                .productCategory(product.getProductCategory())
                .price(product.getPrice())
                .build();
    }
}
