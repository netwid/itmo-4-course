package dev.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortField {
    private String name;
    private String order; // "asc" или "desc"
}
