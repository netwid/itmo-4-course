package dev.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest {
    private PersonFilter filter;
    private SortCriteria sortCriteria;
    private Integer page;
    private Integer size;
}
