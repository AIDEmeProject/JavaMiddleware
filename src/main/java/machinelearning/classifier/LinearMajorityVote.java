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

package machinelearning.classifier;

import machinelearning.classifier.margin.LinearClassifier;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Objects;

/**
 * This class represents a Majority Vote classifier. Given a set of classifiers {H_i}, the majority vote MV outputs:
 *
 *          P(MV(x) = 1) = (1 / N) * \sum_{i=1}^N I(H_i(x) = 1)
 *
 * In other words, the probability of each class is simply the proportion of classifiers agreeing on this class.
 */
public class LinearMajorityVote implements Classifier {
    private Vector bias;
    private Matrix weights;

    public LinearMajorityVote(Vector bias, Matrix weights) {
        Validator.assertEquals(bias.dim(), weights.rows());
        this.bias = bias;
        this.weights = weights;
    }

    @Override
    public Label predict(Vector vector) {
        return margin(vector).iApplyMap(x -> x > 0 ? 1D : -1D).sum() > 0 ? Label.POSITIVE : Label.NEGATIVE;
    }

    @Override
    public Label[] predict(Matrix matrix) {
        Vector sums = margin(matrix).iApplyMap(x -> x > 0 ? 1D : -1D).getRowSums();

        Label[] labels = new Label[sums.dim()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = sums.get(i) > 0 ? Label.POSITIVE : Label.NEGATIVE;
        }

        return labels;
    }

    @Override
    public double probability(Vector vector) {
        return margin(vector).iApplyMap(x -> x > 0 ? 1D : 0D).sum() / bias.dim();
    }

    @Override
    public Vector probability(Matrix matrix) {
        return margin(matrix).iApplyMap(x -> x > 0 ? 1D : 0D).getRowSums().iScalarDivide(bias.dim());
    }

    private Vector margin(Vector vector) {
        return weights.multiply(vector).iAdd(bias);
    }

    private Matrix margin(Matrix matrix) {
        return matrix.multiplyTranspose(weights).iAddRow(bias);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinearMajorityVote that = (LinearMajorityVote) o;
        return Objects.equals(bias, that.bias) &&
                Objects.equals(weights, that.weights);
    }
}
