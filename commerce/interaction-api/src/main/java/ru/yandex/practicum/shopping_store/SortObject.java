package ru.yandex.practicum.shopping_store;

import lombok.Data;

@Data
public class SortObject {
    private String direction;
    private String nullHandling;
    private Boolean ascending;
    private String property;
    private Boolean ignoreCase;
}
