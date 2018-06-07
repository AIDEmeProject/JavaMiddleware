package exceptions;

public class MetricAlreadyExistsException extends RuntimeException {
    public MetricAlreadyExistsException(String name) {
        super("Metric " + name + " already present in object.");
    }
}
