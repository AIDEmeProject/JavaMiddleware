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

package benchmark;

import data.IndexedDataset;
import data.LabeledDataset;
import machinelearning.active.learning.versionspace.KernelVersionSpace;
import machinelearning.active.learning.versionspace.LinearVersionSpace;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.active.learning.versionspace.manifold.selector.WarmUpAndThinSelector;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Label;
import machinelearning.classifier.Learner;
import machinelearning.classifier.MajorityVoteLearner;
import machinelearning.classifier.svm.GaussianKernel;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import utils.linalg.Matrix;
import utils.linprog.LinearProgramSolver;

import java.util.ArrayList;
import java.util.List;

public class ClassifierBenchmark extends AbstractBenchmark {
    private Matrix testData;
    private Classifier classifier;

    @Setup(Level.Trial)
    public void doSetup() {
        classifier = getClassifier(size);
        testData = getRandomMatrix(10000, 2, 7568);
    }

    private Classifier getClassifier(int trainingSetSize) {
        Matrix trainingData = getRandomMatrix(trainingSetSize, 2, 4444);

        Label[] labels = new Label[trainingSetSize];
        for (int i = 0; i < trainingSetSize; i++) {
            labels[i] = i < trainingSetSize / 2 ? Label.POSITIVE : Label.NEGATIVE;
        }

        Learner learner = new MajorityVoteLearner(
                new KernelVersionSpace(
                        new LinearVersionSpace(
                                new HitAndRunSampler.Builder(new WarmUpAndThinSelector(64, 1))
                                        .addSampleCache()
                                        .build(),
                                LinearProgramSolver.getFactory(LinearProgramSolver.LIBRARY.OJALGO)
                        ),
                        new GaussianKernel()
                ),
                8
        );

        List<Long> indexes = new ArrayList<>(trainingSetSize);
        for (long i = 0; i < trainingSetSize; i++) {
            indexes.add(i);
        }

        return learner.fit(new LabeledDataset(new IndexedDataset(indexes, trainingData), labels));
    }

    @Benchmark
    public Label[] predict() {
        return classifier.predict(testData);
    }
}
