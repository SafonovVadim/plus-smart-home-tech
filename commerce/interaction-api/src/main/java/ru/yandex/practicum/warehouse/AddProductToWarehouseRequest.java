package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddProductToWarehouseRequest {


    private UUID productId;

    @Positive
    private Integer quantity;
}
