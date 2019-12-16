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

import org.apache.commons.math3.util.FastMath;
import smile.math.kernel.MercerKernel;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;


/**
 * The gaussian kernel is defined as:
 *
 *    \( k(x,y) = \exp \left( -\gamma \Vert x - y \Vert^2 \right) \)
 *
 * where gamma is a positive number. In practice, one usually chooses gamma = 1.0 / num_features.
 */
public class GaussianKernel extends Kernel {
    /**
     * gamma parameter
     */
    private double gamma;

    private static final DistanceKernel distanceKernel = new DistanceKernel();

    /**
     * @param gamma gamma parameter of gaussian kernel
     * @throws IllegalArgumentException if gamma is not positive
     */
    public GaussianKernel(double gamma) {
        Validator.assertPositive(gamma);
        this.gamma = gamma;
    }

    /**
     * Uses the default value of 1.0 / num_features for gamma
     */
    public GaussianKernel() {
        this.gamma = 0;
    }

    private double getGamma(int dim) {
        return this.gamma > 0 ? this.gamma : (1.0 / dim);
    }

    private double gaussianMap(double sqDistance, double gamma) {
        return FastMath.exp(-gamma * sqDistance);
    }

    @Override
    public double compute(Vector x, Vector y) {
        final double gamma = getGamma(y.dim());
        return gaussianMap(distanceKernel.compute(x, y), gamma);
    }

    @Override
    public Vector compute(Matrix xs, Vector y) {
        final double gamma = getGamma(y.dim());
        return distanceKernel.compute(xs, y).iApplyMap(x -> gaussianMap(x, gamma));
    }

    @Override
    public Matrix compute(Matrix xs, Matrix ys) {
        final double gamma = getGamma(xs.cols());
        return distanceKernel.compute(xs, ys).iApplyMap(x -> gaussianMap(x, gamma));
    }

    @Override
    MercerKernel<double[]> getSmileKernel(int dim) {
        double sig = this.gamma == 0 ? 1.0 / dim : this.gamma;
        double std = 1 / Math.sqrt(2 * sig);
        return new smile.math.kernel.GaussianKernel(std);
    }

    @Override
    public String toString() {
        return "Gaussian Kernel gamma=" + gamma;
    }
}
