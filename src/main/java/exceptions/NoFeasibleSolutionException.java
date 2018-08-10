package exceptions;

public class NoFeasibleSolutionException extends RuntimeException {
    public NoFeasibleSolutionException() {
        super("No feasible solution exists for linear programming problem.");
    }
}
