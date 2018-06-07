package exceptions;

public class MetricNotFoundException extends RuntimeException {
    public MetricNotFoundException(String name) {
        super("Object does not contain the requested metric: " + name);
    }
}
