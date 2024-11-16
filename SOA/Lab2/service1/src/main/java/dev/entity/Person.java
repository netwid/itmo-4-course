package dev.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Имя не может быть null")
    @NotEmpty(message = "Имя не может быть пустым")
    private String name;

    @Embedded
    @NotNull(message = "Координаты не могут быть null")
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(nullable = false)
    @NotNull(message = "Высота не может быть null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Высота должна быть больше 0")
    private Float height;

    @Column(nullable = false)
    @NotNull(message = "Вес не может быть null")
    @Min(value = 1, message = "Вес должен быть больше 0")
    private Integer weight;

    @Column(nullable = false, unique = true, length = 48)
    @NotNull(message = "PassportID не может быть null")
    @Size(min = 10, max = 48, message = "PassportID должен содержать от 10 до 48 символов")
    private String passportID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Национальность не может быть null")
    private Country nationality;

    @Embedded
    @NotNull(message = "Местоположение не может быть null")
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column
    private EyeColor eyeColor;

    @Enumerated(EnumType.STRING)
    @Column
    private HairColor hairColor;
}
