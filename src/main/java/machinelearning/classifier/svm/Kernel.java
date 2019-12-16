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

package machinelearning.classifier.svm;

import smile.math.kernel.MercerKernel;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

/**
 * A kernel is any function k(x,y) satisfying the Mercer conditions:
 *
 *      - Symmetry: k(x,y) = k(y,x)
 *      - Positivity: for all \( \{x_1, \ldots, x_n\}, \) the matrix \(K_{ij} = k(x_i, x_j\) is positive definite
 */
public abstract class Kernel {
    /**
     * @param x: a vector
     * @param y: a vector
     * @return the kernel function applied on the input vectors: k(x,y)
     */
    public abstract double compute(Vector x, Vector y);

    /**
     * @param xs a collection of data points
     * @param y a data point
     * @return computes the vector \([k(x_1, y), ..., k(x_n, y)]\)
     */
    public Vector compute(Matrix xs, Vector y) {
        Validator.assertEquals(xs.cols(), y.dim());

        double[] result = new double[xs.rows()];
        for (int i = 0; i < result.length; i++) {
            result[i] = compute(xs.getRow(i), y);
        }
        return Vector.FACTORY.make(result);
    }

    /**
     * @param xs a collection of data points
     * @param ys a second collection of data points
     * @return computes the matrix \([k(x_i, y_j)]\)
     */
    public Matrix compute(Matrix xs, Matrix ys) {
        Validator.assertEquals(xs.cols(), ys.cols());

        double[][] result = new double[xs.rows()][ys.rows()];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = compute(xs.getRow(i), ys.getRow(j));
            }
        }
        return Matrix.FACTORY.make(result);
    }

    /**
     * @param xs a collection of data points
     * @return the kernel matrix \(K_{ij} = k(x_i, x_j)\)
     */
    public  Matrix compute(Matrix xs){
        return compute(xs, xs);
    }

    /**
     * Utility method for getting the Smile's equivalent kernel function
     * @see SvmLearner
     */
    MercerKernel<double[]> getSmileKernel(int dim) {
        throw new RuntimeException("Kernel not supported");
    }
}
