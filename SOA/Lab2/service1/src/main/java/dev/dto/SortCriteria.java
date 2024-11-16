package dev.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SortCriteria {
    private List<SortField> fields;
}

