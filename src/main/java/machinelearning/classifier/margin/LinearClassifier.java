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

package machinelearning.classifier.margin;

import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Objects;

/**
 * A linear classifier. It is defined by two parameters: bias and weight. Predictions are made as in Logistic Regression:
 *
 *      P(y = 1 | x) = sigmoid(bias + weight^T x)
 */
public class LinearClassifier extends MarginClassifier {

    private final HyperPlane hyperplane;

    /**
     * @param bias: bias parameters
     * @param weights: weight vector
     * @throws IllegalArgumentException if weights are empty
     */
    public LinearClassifier(double bias, Vector weights) {
        this.hyperplane = new HyperPlane(bias, weights);
    }

    /**
     * @return dimension of hyperplane
     */
    public int dim() { return hyperplane.dim(); }

    /**
     * Compute bias + weight^T x
     */
    @Override
    public double margin(Vector x){
        return hyperplane.margin(x);
    }

    @Override
    public Vector margin(Matrix xs) {
        return hyperplane.margin(xs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinearClassifier that = (LinearClassifier) o;
        return Objects.equals(hyperplane, that.hyperplane);
    }
}
