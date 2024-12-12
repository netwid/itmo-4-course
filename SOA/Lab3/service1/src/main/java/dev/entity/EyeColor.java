package dev.entity;

import lombok.Getter;

@Getter
public enum EyeColor {
    BLUE("Голубой"),
    BROWN("Карий"),
    GREEN("Зеленый"),
    GRAY("Серый");

    private final String label;

    EyeColor(String label) {
        this.label = label;
    }

}
