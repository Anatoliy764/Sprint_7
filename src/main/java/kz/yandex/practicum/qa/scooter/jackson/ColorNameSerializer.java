package kz.yandex.practicum.qa.scooter.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import kz.yandex.practicum.qa.scooter.common.Color;

import java.io.IOException;

public class ColorNameSerializer extends StdSerializer<Color> {
    public ColorNameSerializer() {
        super(Color.class);
    }

    @Override
    public void serialize(Color color,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeString(color.name());
    }
}
