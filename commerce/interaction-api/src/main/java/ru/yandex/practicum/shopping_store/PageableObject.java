package ru.yandex.practicum.shopping_store;

import lombok.Data;

@Data
public class PageableObject {
    private Long offset;
    private SortObject sort;
    private Boolean unpaged;
    private Boolean paged;
    private Integer pageNumber;
    private Integer pageSize;
}
