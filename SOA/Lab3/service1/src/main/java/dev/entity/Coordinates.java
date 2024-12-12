package dev.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Embeddable
public class Coordinates {

    @Column(name = "coordinate_x")
    private long x;

    @Column(name = "coordinate_y", nullable = false)
    @NotNull(message = "Поле Y не может быть null")
    private Double y;
}
