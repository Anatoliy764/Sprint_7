package kz.yandex.practicum.qa.scooter.domain.order.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonFormat;
import kz.yandex.practicum.qa.scooter.common.Color;
import kz.yandex.practicum.qa.scooter.jackson.ColorCollectionDeserializer;
import kz.yandex.practicum.qa.scooter.jackson.ColorNameCollectionSerializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    Long id;
    String firstName;
    String lastName;
    String address;
    Integer metroStation;
    String phone;
    Integer rentTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate deliveryDate;
    String comment;

    @JsonSerialize(using = ColorNameCollectionSerializer.class)
    @JsonDeserialize(using = ColorCollectionDeserializer.class)
    List<Color> color = new LinkedList<>();

    public Order addColor(Color color) {
        this.color.add(color);
        return this;
    }

    public Order removeColor(Color color) {
        this.color.remove(color);
        return this;
    }
}
