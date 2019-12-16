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

package machinelearning.bayesian;

import data.LabeledDataset;
import explore.user.UserLabel;
import machinelearning.active.learning.versionspace.VersionSpace;
import machinelearning.classifier.LinearMajorityVote;
import machinelearning.classifier.margin.LinearClassifier;
import utils.RandomState;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Arrays;


/**
 * A Bayesian Version Space maintains a probability distribution over the parameters of a classifier (for example, the weights
 * of a Linear Classifier) instead of maintaining cuts. Such distribution is defined by means of the Bayes rule, and it is
 * fitted over labeled data. An advantage of this method is it supports noisy labeling, in contrast with usual Version
 * Space algorithms.
 *
 * In the particular case of this class, we sample Linear Classifiers through this Bayesian approach.
 *
 * @see StanLogisticRegressionSampler
 */
public class BayesianLinearVersionSpace implements VersionSpace {
    /**
     * Whether to fit the intercept
     */
    private final boolean addIntercept;

    /**
     * Stan sampler
     */
    private final StanLogisticRegressionSampler sampler;

    /**
     * @param warmup: number of initial samples to skip
     * @param thin: only keep every "thin" sample after warm-up phase
     * @param sigma: standard deviation of gaussian prior
     * @param addIntercept: whether to fit intercept
     * @throws IllegalArgumentException if warmup, thin, or sigma are negative
     */
    public BayesianLinearVersionSpace(int warmup, int thin, double sigma, boolean addIntercept) {
        Validator.assertPositive(warmup);
        Validator.assertPositive(thin);
        Validator.assertPositive(sigma);

        this.addIntercept = addIntercept;
        this.sampler = new StanLogisticRegressionSampler(warmup, thin, sigma);
    }

    @Override
    public LinearMajorityVote sample(LabeledDataset labeledPoints, int numSamples) {
        Validator.assertPositive(numSamples);

        Matrix data = labeledPoints.getData();
        if (addIntercept) {
            data = data.addBiasColumn();
        }

        int[] ys = Arrays.stream(labeledPoints.getLabels())
                .mapToInt(UserLabel::asBinary)
                .toArray();

        double[][] samples = sampler.run(numSamples, data.toArray(), ys, RandomState.newInstance().nextInt());

        Vector bias = Vector.FACTORY.zeros(samples.length);
        Matrix weights = Matrix.FACTORY.make(samples);

        if (addIntercept) {
            bias = weights.getCol(0);
            weights = weights.getColSlice(1, weights.cols());
        }

        return new LinearMajorityVote(bias, weights);
    }
}
