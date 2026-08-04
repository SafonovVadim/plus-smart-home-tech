package ru.yandex.practicum.warehouse;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
public class AssemblyProductsForOrderRequest {

    private UUID orderId;
    private Map<UUID, Long> products;
}
