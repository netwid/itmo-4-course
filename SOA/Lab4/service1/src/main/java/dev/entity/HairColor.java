package dev.entity;

import lombok.Getter;

@Getter
public enum HairColor {
    BLONDE("Блондин"),
    RED("Рыжий"),
    BLACK("Черный"),
    GRAY("Седой");

    private final String label;

    HairColor(String label) {
        this.label = label;
    }

}
