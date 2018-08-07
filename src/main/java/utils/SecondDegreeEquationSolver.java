package utils;

/**
 * a x^2 + 2 b x + c
 */
public class SecondDegreeEquationSolver {
    public static class SecondDegreeEquationSolution {
        private double left;
        private double right;

        public SecondDegreeEquationSolution(double left, double right) {
            this.left = left;
            this.right = right;
        }

        public double getLeft() {
            return left;
        }

        public double getRight() {
            return right;
        }
    }

    public static SecondDegreeEquationSolution solve(double a, double b, double c){
        double delta = getDelta(a, b, c);

        if (delta <= 0){
            throw new RuntimeException("Equation does not have two solutions: [a=" + a + ", b=" +b + ", c=" + c + "], delta = " + delta);
        }

        double sqDelta = Math.sqrt(delta);
        return new SecondDegreeEquationSolution ((-b - sqDelta) / a , (-b + sqDelta) / a);
    }

    private static double getDelta(double a, double b, double c){
        return b*b - a*c;
    }
}
