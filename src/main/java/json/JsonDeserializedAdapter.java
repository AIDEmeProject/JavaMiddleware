package json;

import com.google.gson.*;

import java.lang.reflect.Type;

public abstract class JsonDeserializedAdapter<T> implements com.google.gson.JsonDeserializer<T> {
    private static String PROP_NAME ="name";

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        String identifier = json.getAsJsonObject().getAsJsonPrimitive(PROP_NAME).getAsString();
        String classPath = getPackagePrefix() + "." + getCanonicalName(identifier);

        try {
            Class<T> cls = (Class<T>) Class.forName(classPath);
            return (T) context.deserialize(json, cls);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't find class: " + classPath);
        }
    }

    public abstract String getPackagePrefix();

    public abstract String getCanonicalName(String identifier);
}
