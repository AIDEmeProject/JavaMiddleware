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

package machinelearning.active.learning.versionspace;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.KernelMajorityVote;
import machinelearning.classifier.margin.KernelClassifier;
import machinelearning.classifier.svm.Kernel;
import utils.Validator;
import utils.linalg.Matrix;

/**
 * This class defines the Version Space for the {@link KernelClassifier} classifier. It is defined by the set of
 * equations:
 *
 *  \( y_i  \left(b + \sum_i \alpha_i^t k(x_i, x_j) \right) &gt; 0 \)
 *
 * Note that its dimension equals the number of support vectors (which increases as an Active Learning algorithm runs).
 * Sampling from this version space can be done in the same way as for the {@link LinearVersionSpace}, the only difference
 * is we need to construct the Kernel Matrix of the labeled data beforehand.
 *
 * @see KernelClassifier
 * @see LinearVersionSpace
 * @see HitAndRunSampler
 */
public class KernelVersionSpace implements VersionSpace {
    /**
     * {@link LinearVersionSpace} instance used for sampling
     */
    private final VersionSpace versionSpace;

    /**
     * {@link Kernel} function
     */
    private final Kernel kernel;

    /**
     * @param versionSpace: linear version space instance
     * @param kernel: the kernel function
     * @throws NullPointerException if sampler or kernel is null
     */
    public KernelVersionSpace(VersionSpace versionSpace, Kernel kernel) {
        Validator.assertNotNull(versionSpace);
        Validator.assertNotNull(kernel);
        this.versionSpace = versionSpace;
        this.kernel = kernel;
    }

    @Override
    public KernelMajorityVote sample(LabeledDataset labeledPoints, int numSamples) {
        Matrix kernelMatrix = kernel.compute(labeledPoints.getData());
        LabeledDataset kernelLabeledPoints = labeledPoints.copyWithSameIndexesAndLabels(kernelMatrix);
        Classifier linearMajorityVote = versionSpace.sample(kernelLabeledPoints, numSamples);
        return new KernelMajorityVote(linearMajorityVote, labeledPoints.getData(), kernel);
    }
}
