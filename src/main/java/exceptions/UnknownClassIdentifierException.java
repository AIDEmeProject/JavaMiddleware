package exceptions;

public class UnknownClassIdentifierException extends RuntimeException {
    public UnknownClassIdentifierException(String className, String identifier) {
        super("Unknown identifier for " + className + " class: " + identifier);
    }
}
