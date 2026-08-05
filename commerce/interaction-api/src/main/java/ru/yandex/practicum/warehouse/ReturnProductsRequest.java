package ru.yandex.practicum.warehouse;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Data
@Builder
public class ReturnProductsRequest {
    private Map<UUID, Long> returnProducts;
}
