package ru.yandex.practicum.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.exception.*;

@Slf4j
@RequiredArgsConstructor
public class CustomErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String body = feign.Util.toString(response.body().asReader());
            ApiErrorResponse error = objectMapper.readValue(body, ApiErrorResponse.class);

            String userMessage = error.getUserMessage();

            // 1. NoProductsInShoppingCartException
            if (userMessage != null && (userMessage.contains("нет товаров") || userMessage.contains("корзина пуста"))) {
                return new NoProductsInShoppingCartException(error.getMessage(), userMessage);
            }

            // 2. NoSpecifiedProductInWarehouseException
            if (userMessage != null && userMessage.contains("товар не найден на складе")) {
                return new NoSpecifiedProductInWarehouseException(error.getMessage(), userMessage);
            }

            // 3. NotAuthorizedUserException
            if (userMessage != null && (userMessage.contains("не авторизован") || userMessage.contains("авторизации"))) {
                return new NotAuthorizedUserException(error.getMessage(), userMessage);
            }

            // 4. ProductInShoppingCartLowQuantityInWarehouse
            if (userMessage != null && (userMessage.contains("мало на складе") || userMessage.contains("недостаточно товара"))) {
                return new ProductInShoppingCartLowQuantityInWarehouse(error.getMessage(), userMessage);
            }

            // 5. ProductNotFoundException
            if (response.status() == 404 || (userMessage != null && userMessage.contains("товар не найден"))) {
                return new ProductNotFoundException(error.getMessage(), userMessage);
            }

            // 6. SpecifiedProductAlreadyInWarehouseException
            if (userMessage != null && userMessage.contains("уже есть") || userMessage.contains("дубликат")) {
                return new SpecifiedProductAlreadyInWarehouseException(error.getMessage(), userMessage);
            }

            // По умолчанию — общий RuntimeException
            return new RuntimeException(
                    "Unexpected API error: " + error.getMessage(),
                    new Throwable(error.getLocalizedMessage())
            );

        } catch (Exception e) {
            log.warn("Failed to parse error response", e);
            return defaultDecoder.decode(methodKey, response);
        }
    }
}
