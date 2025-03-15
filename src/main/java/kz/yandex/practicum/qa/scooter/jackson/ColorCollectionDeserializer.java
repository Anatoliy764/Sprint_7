package kz.yandex.practicum.qa.scooter.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import kz.yandex.practicum.qa.scooter.common.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ColorCollectionDeserializer extends StdDeserializer<Collection<Color>> {

    private final ColorDeserializer colorDeserializer = new ColorDeserializer();

    protected ColorCollectionDeserializer() {
        super(List.class);
    }

    @Override
    public Collection<Color> deserialize(JsonParser jsonParser,
                                   DeserializationContext context) throws IOException {

        // Parse the JSON array
        List<Color> colorList = new ArrayList<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        // Check if JSON node is an array
        if (node.isArray()) {
            for (JsonNode element : node) {
                // Deserialize each element using ColorDeserializer
                JsonParser elementParser = element.traverse(jsonParser.getCodec());
                colorList.add(colorDeserializer.deserialize(elementParser, context));
            }
        } else {
            throw new JsonParseException(jsonParser, "Expected JSON array for List<Color>");
        }

        return colorList;
    }
}
