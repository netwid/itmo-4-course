package dev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LocationDTO {
    private Integer x;
    private Float y;
    private Float z;
    private String name;
}
