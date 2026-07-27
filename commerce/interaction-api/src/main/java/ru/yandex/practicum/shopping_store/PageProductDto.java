package ru.yandex.practicum.shopping_store;

import lombok.Data;

import java.util.List;

@Data
public class PageProductDto {
    private List<ProductDto> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;
    private Boolean first;
    private Boolean last;
    private Integer numberOfElements;
    private Boolean empty;
    private SortObject sort;
    private PageableObject pageable;
}
