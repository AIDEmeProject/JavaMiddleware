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
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)  // measure the Average Time of execution of each method
@OutputTimeUnit(TimeUnit.MICROSECONDS)  // Return average time in MICROSECONDS
@State(Scope.Benchmark)  // All threads running a benchmark share the same state (i.e. the below class attributes)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class AbstractBenchmark {
    // run benchmarks with different matrix and vector sizes
    @Param({"16", "32", "64", "128", "256", "512", "1024"})
    public int size;

    public double PI = 3.1415;  // do NOT set as final (or Constant Folding could take place)

    // compute a random vector
    static Vector getRandomVector(int size, long seed) {
        Random random = new Random(seed);
        double[] values = new double[size];

        for (int i = 0; i < size; i++) {
            values[i] = random.nextDouble();
        }
        return Vector.FACTORY.make(values);
    }

    // compute a random matrix
    static Matrix getRandomMatrix(int rows, int cols, long seed) {
        Random random = new Random(seed);
        double[][] values = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i][j] = random.nextDouble();
            }
        }

        return Matrix.FACTORY.make(values);
    }

    // compute a random SPD matrix
    static Matrix getRandomSPDMatrix(int size, long seed) {
        Matrix A = getRandomMatrix(size, size, seed);
        A = A.add(A.transpose()).scalarMultiply(0.5);
        A = A.add(Matrix.FACTORY.identity(size).scalarMultiply(size));
        return A;
    }
}
