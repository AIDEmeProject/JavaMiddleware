package exceptions;

public class SecondDegreeEquationSolverFailed extends RuntimeException {
    public SecondDegreeEquationSolverFailed(double a, double b, double c, double delta) {
        super(String.format("Second degree equation does not have two solutions: a = %f, b = %f, c = %f, delta = %f", a, b, c, delta));
    }
}
