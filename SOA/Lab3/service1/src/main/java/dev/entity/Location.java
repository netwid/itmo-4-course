package dev.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Embeddable
public class Location {

    @Column(name = "location_x", nullable = false)
    @NotNull(message = "Координата X не может быть null")
    private Integer x;

    @Column(name = "location_y")
    private float y;

    @Column(name = "location_z")
    private float z;

    @Column(name = "location_name", nullable = false)
    @NotNull(message = "Название местоположения не может быть null")
    private String name;
}
