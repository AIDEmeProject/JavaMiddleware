/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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
