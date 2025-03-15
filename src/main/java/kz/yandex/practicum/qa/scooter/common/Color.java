package kz.yandex.practicum.qa.scooter.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Color {
    GREY(" #808080"),
    BLACK("#000000"),
    PERMANENT_GERANIUM_LAKE("#D92B2C"),
    GREEN("#4DBE52"),
    CELTIC_BLUE("#2C75C4"),
    PICTON_BLUE("#4DC6F4"),
    ORANGE("#F07025"),
    VIOLET("#89339E"),
    YELLOW("#FBC81E"),
    LIGHT_GREY("#9F9F9F"),
    LIGHT_GREEN("#A8D92D"),
    AQUA("#80D4C9"),
    LIGHT_STEEL_BLUE("#B0BFE7"),
    PINK("#efadb5")
    ;
    final String hex;

    public static Color random() {
        return values()[(int) (Math.random() * values().length)];
    }

    public static Color valueOfHex(String hex) {
        for (Color color : values()) {
            if (color.getHex().equals(hex)) {
                return color;
            }
        }
        return null;
    }
}
