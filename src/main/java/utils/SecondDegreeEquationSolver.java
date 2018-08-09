package utils;

import exceptions.SecondDegreeEquationSolverFailed;

/**
 * This utility class is concerned with finding the two roots of a second degree equation:
 *
 *                      \( a x^2 + 2 b x + c = 0 \)
 *
 * In our applications, only the two real, distinct solutions case is of interest. So. we do not deal with the one or
 * zero real solutions scenarios.
 */
public class SecondDegreeEquationSolver {
    /**
     * This inner class holds the two solution of the second degree equation.
     */
    public static class SecondDegreeEquationSolution {
        private double first;
        private double second;

        // private so clients cannot instantiate
        private SecondDegreeEquationSolution(double first, double second) {
            this.first = Math.min(first, second);
            this.second = Math.max(first, second);
        }

        /**
         * @return the smallest root
         */
        public double getFirst() {
            return first;
        }

        /**
         * @return the largest root
         */
        public double getSecond() {
            return second;
        }
    }

    /**
     * @param a: coefficient of the second-degree term
     * @param b: half the coefficient of the linear term
     * @param c: independent term
     * @return the two real solutions of the equation  \( a x^2 + 2 b x + c = 0 \)
     * @throws SecondDegreeEquationSolverFailed if this equation has one or less real solutions
     */
    public static SecondDegreeEquationSolution solve(double a, double b, double c){
        double delta = getDelta(a, b, c);

        if (delta <= 0 || a == 0){
            throw new SecondDegreeEquationSolverFailed(a, b, c, delta);
        }

        double sqDelta = Math.sqrt(delta);
        return new SecondDegreeEquationSolution ((-b - sqDelta) / a , (-b + sqDelta) / a);
    }

    private static double getDelta(double a, double b, double c){
        return b*b - a*c;
    }
}
