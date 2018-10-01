package io.json;

import com.google.gson.*;
import utils.linalg.Vector;

import java.lang.reflect.Type;

public class VectorAdapter implements JsonDeserializer<Vector>, JsonSerializer<Vector> {
    @Override
    public Vector deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray array = jsonElement.getAsJsonArray();
        double[] values = new double[array.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = array.get(i).getAsDouble();
        }
        return Vector.FACTORY.make(values);
    }

    @Override
    public JsonElement serialize(Vector vector, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray valuesArray = new JsonArray();

        for (double value : vector.toArray()) {
            valuesArray.add(new JsonPrimitive(value));
        }

        return valuesArray;
    }
}
