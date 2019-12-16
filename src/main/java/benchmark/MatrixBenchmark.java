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

import org.openjdk.jmh.annotations.*;
import utils.linalg.CholeskyDecomposition;
import utils.linalg.EigenvalueDecomposition;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.concurrent.TimeUnit;


public class MatrixBenchmark extends AbstractBenchmark {

    private Matrix a, b, spd;
    private Vector c;

    @Setup(Level.Trial)  // fixture method. Used for initializing the class attributes. Executed once before the entire benchmark run
    public void doSetup() {
        a = getRandomMatrix(size, size, 1234);
        b = getRandomMatrix(size, size,4321);
        c = getRandomVector(size, 5678);
        spd = getRandomSPDMatrix(size, 8765);
    }

    @Benchmark
    public Matrix add() {
        return a.add(b);
    }

    @Benchmark
    public Matrix scalarMultiply() {
        return a.scalarMultiply(PI);
    }

    @Benchmark
    public Vector vectorMultiply() {
        return a.multiply(c);
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    public Matrix matrixMultiply() {
        return a.matrixMultiply(b);
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    public Matrix multiplyTranspose() {
        return a.multiplyTranspose(b);
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    public double eigen() {
        return new EigenvalueDecomposition(spd).getEigenvalue(0);
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    public Matrix cholesky() {
        return new CholeskyDecomposition(spd).getL();
    }

    @Benchmark
    public Matrix addRow() {
        return a.addRow(c);
    }

    @Benchmark
    public Matrix iAddRow() {
        return a.iAddRow(c);
    }

    @Benchmark
    public Matrix addColumn() {
        return a.addColumn(c);
    }

    @Benchmark
    public Matrix iAddColumn() {
        return a.iAddColumn(c);
    }
}
