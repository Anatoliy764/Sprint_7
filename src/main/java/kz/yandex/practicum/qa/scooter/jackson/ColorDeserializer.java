package kz.yandex.practicum.qa.scooter.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import kz.yandex.practicum.qa.scooter.common.Color;

import java.io.IOException;

public class ColorDeserializer extends StdDeserializer<Color> {
    protected ColorDeserializer() {
        super(Color.class);
    }

    @Override
    public Color deserialize(JsonParser jsonParser,
                             DeserializationContext deserializationContext) throws IOException, JacksonException {

        String colorText = jsonParser.getText();

        Color color = Color.valueOfHex(colorText);

        if (color == null) {
            try {
                color = Color.valueOf(colorText);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException(jsonParser, e.getMessage(), jsonParser.getCurrentLocation(), e);
            }
        }

        return color;
    }
}
