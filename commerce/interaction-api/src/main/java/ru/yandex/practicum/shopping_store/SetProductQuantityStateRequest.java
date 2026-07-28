package ru.yandex.practicum.shopping_store;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SetProductQuantityStateRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private String quantityState;
}
