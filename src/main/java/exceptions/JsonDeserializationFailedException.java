package exceptions;

import java.lang.reflect.Type;

public class JsonDeserializationFailedException extends RuntimeException {
    public <T> JsonDeserializationFailedException(Class<T> type) {
        super("Failed to deserialize JSON string as object of class " + type);
    }

    public <T> JsonDeserializationFailedException(Type type) {
        super("Failed to deserialize JSON string as object of class " + type);
    }
}
