package exceptions;

public class UnboundedSolutionException extends RuntimeException {
    public UnboundedSolutionException() {
        super("Unbounded linear programming problem.");
    }
}
