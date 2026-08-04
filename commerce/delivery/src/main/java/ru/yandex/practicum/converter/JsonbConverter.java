package ru.yandex.practicum.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.yandex.practicum.warehouse.AddressDto;

@Converter(autoApply = true)
public class JsonbConverter implements AttributeConverter<AddressDto, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(AddressDto attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting AddressDto to JSON", e);
        }
    }

    @Override
    public AddressDto convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, AddressDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to AddressDto", e);
        }
    }
}
