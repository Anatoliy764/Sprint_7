package kz.yandex.practicum.qa.scooter.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import kz.yandex.practicum.qa.scooter.common.Color;

import java.io.IOException;
import java.util.Collection;

public class ColorNameCollectionSerializer extends StdSerializer<Collection<Color>> {

    public ColorNameCollectionSerializer() {
        super(TypeFactory.defaultInstance().constructCollectionType(Collection.class, Color.class));
    }

    @Override
    public void serialize(Collection<Color> colors,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        // Start writing JSON array
        jsonGenerator.writeStartArray();

        // Iterate over each Color and serialize it
        for (Color color : colors) {
            // Serialize each Color into its name string
            jsonGenerator.writeString(color.name());
        }

        // End JSON array
        jsonGenerator.writeEndArray();
    }
}
