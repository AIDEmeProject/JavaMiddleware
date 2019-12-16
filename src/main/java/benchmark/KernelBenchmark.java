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

import machinelearning.classifier.svm.DistanceKernel;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.LinearKernel;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import utils.linalg.Matrix;


public class KernelBenchmark extends AbstractBenchmark {
    private Kernel linearKernel = new LinearKernel();
    private Kernel gaussianKernel = new GaussianKernel();
    private Kernel distanceKernel = new DistanceKernel();

    private Matrix a, b;

    @Setup(Level.Trial)
    public void doSetup() {
        a = getRandomMatrix(50, 2, 3333);
        b = getRandomMatrix(size, 2, 4444);
    }

    @Benchmark
    public Matrix distance() {
        return distanceKernel.compute(a, b);
    }

    @Benchmark
    public Matrix linear() {
        return linearKernel.compute(a, b);
    }

    @Benchmark
    public Matrix gaussian() {
        return gaussianKernel.compute(a, b);
    }
}
