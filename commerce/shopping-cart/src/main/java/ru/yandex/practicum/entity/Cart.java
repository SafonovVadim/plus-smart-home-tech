package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "state", nullable = false)
    private String state;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<CartProduct> cartProducts = new ArrayList<>();
}
