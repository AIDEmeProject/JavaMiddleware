package exceptions;

import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class JsonDeserializationFailedException extends RuntimeException {
    public <T> JsonDeserializationFailedException(Class<T> type, JsonParseException ex) {
        this("Failed to deserialize JSON string as object of class " + type, ex);
    }

    public JsonDeserializationFailedException(Type type, JsonParseException ex) {
        this("Failed to deserialize JSON string as object of class " + type, ex);
    }

    private JsonDeserializationFailedException(String message, JsonParseException ex) {
        super(message, ex);
    }
}
