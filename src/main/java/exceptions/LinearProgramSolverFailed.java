package exceptions;

public class LinearProgramSolverFailed extends RuntimeException {
    public LinearProgramSolverFailed() {
        super("Linear Programming solver failed.");
    }
}
