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

import data.DataPoint;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import utils.linalg.Matrix;
import utils.linalg.Vector;

/**
 * A margin classifier is defined by:
 *
 *      \( h(x) = sign( T(x) ) \)
 *
 * where T(x) is a "margin" function. In other words, T(x) returns the signed distance of "x" to the decision boundary
 * (which is given by \( \{x : T(x) = 0\} \).
 *
 * Probability calculations are made through application of the sigmoid function to the margin.
 */
public abstract class MarginClassifier implements Classifier {
    /**
     * @param x: a feature vector
     * @return the margin of this point
     */
    public abstract double margin(Vector x);

    /**
     * @param xs: a matrix of feature vectors (one per row)
     * @return a Vector containing the margins of each feature vector
     */
    public abstract Vector margin(Matrix xs);

    /**
     * @param point: a data point
     * @return the margin of this point
     */
    public final double margin(DataPoint point){
        return margin(point.getData());
    }

    @Override
    public double probability(Vector vector) {
        return sigmoid(margin(vector));
    }

    @Override
    public Vector probability(Matrix matrix) {
        return margin(matrix).iApplyMap(MarginClassifier::sigmoid);
    }

    private static double sigmoid(double value) {
        return 1.0 / (1.0 + Math.exp(-value));
    }

    /**
     * @param point: a data point
     * @return sign( margin(x) )
     */
    @Override
    public final Label predict(Vector point) {
        return Label.fromSign(margin(point));
    }
}
