package exceptions;

public class IncompatibleMetricsException extends RuntimeException {
    public IncompatibleMetricsException() {
        super("Metrics do not contain the same names.");
    }
}
