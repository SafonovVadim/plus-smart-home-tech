package ru.yandex.practicum.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductReturnRequest {

    private UUID orderId;

    @NotEmpty
    private Map<UUID, Long> products;
}