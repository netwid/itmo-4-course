package dev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchRequest {
    private PersonFilter personFilter;
    private SortCriteria sortCriteria;
    private Integer page;
    private Integer size;
}
