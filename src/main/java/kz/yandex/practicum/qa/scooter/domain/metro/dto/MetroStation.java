package kz.yandex.practicum.qa.scooter.domain.metro.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kz.yandex.practicum.qa.scooter.common.Color;
import kz.yandex.practicum.qa.scooter.jackson.ColorDeserializer;
import kz.yandex.practicum.qa.scooter.jackson.ColorHexSerializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MetroStation {
    Integer number;
    String name;

    @JsonSerialize(using = ColorHexSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    Color color;
}
