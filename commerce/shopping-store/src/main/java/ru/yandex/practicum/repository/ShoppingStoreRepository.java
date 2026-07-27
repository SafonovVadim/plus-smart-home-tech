package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.shopping_store.ProductCategory;
import ru.yandex.practicum.shopping_store.ProductState;

import java.util.UUID;

public interface ShoppingStoreRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByProductStateAndProductCategory(ProductState state, ProductCategory category, Pageable pageable);

    Page<Product> findByProductState(ProductState state, Pageable pageable);
}
