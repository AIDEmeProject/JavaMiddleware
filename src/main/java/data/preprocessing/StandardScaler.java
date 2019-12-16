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

package data.preprocessing;

import data.IndexedDataset;
import explore.statistics.Statistics;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

/**
 * This class is responsible for standardizing each column of a double matrix; in other words, after processing, each
 * column will have mean zero and unit variance. Mean and standard deviation computation is based on Welford's method,
 * also present on Knuth's The Art Of Computer Programming book, Vol 2.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance">Variance computation methods wiki</a>
 */
public class StandardScaler {
    /**
     * Mean of each column
     */
    private Vector mean;

    /**
     * Standard deviation of each column
     */
    private Vector std;

    private StandardScaler(Vector mean, Vector std) {
        this.mean = mean;
        this.std = std;
    }

    /**
     * Compute the mean and standard deviation of each column in the input collection
     * @param points: collection of points to fit
     * @return a Standard Scaler object fitted on the input data
     * @throws IllegalArgumentException if standard deviation of any column is zero
     */
    public static StandardScaler fit(Matrix points){
        Statistics[] statistics = points.columnStatistics();

        Vector mean = getMeanFromStatistics(statistics);
        Vector std = getStandardDeviationFromStatistics(statistics);

        return new StandardScaler(mean, std);
    }

    /**
     * Compute the mean and standard deviation of each column in dataset
     * @param dataset: dataset to fit
     * @return a Standard Scaler object fitted on the input data
     * @throws IllegalArgumentException if standard deviation of any column is zero
     */
    public static StandardScaler fit(IndexedDataset dataset) {
        return StandardScaler.fit(dataset.getData());
    }

    public static Matrix fitAndTransform(Matrix points) {
        return fit(points).transform(points);
    }

    public static IndexedDataset fitAndTransform(IndexedDataset dataset) {
        Matrix scaledData = StandardScaler.fitAndTransform(dataset.getData());
        return dataset.copyWithSameIndexes(scaledData);
    }

    private static Vector getMeanFromStatistics(Statistics[] statistics) {
        double[] mean = new double[statistics.length];

        for (int i = 0; i < statistics.length; i++) {
            mean[i] = statistics[i].getMean();
        }

        return Vector.FACTORY.make(mean);
    }

    private static Vector getStandardDeviationFromStatistics(Statistics[] statistics) {
        double[] std = new double[statistics.length];

        for (int i = 0; i < statistics.length; i++) {
            std[i] = statistics[i].getStandardDeviation();
            Validator.assertPositive(std[i]);
        }

        return Vector.FACTORY.make(std);
    }

    /**
     * @param dataPoints: data to standardize
     * @return a new standardized collection of points
     * @throws IllegalArgumentException if data points have different dimension from fitted data
     */
    public Matrix transform(Matrix dataPoints){
        return dataPoints.subtractRow(mean).divideRow(std);
    }

    /**
     * @param dataset: data to standardize
     * @return a new standardized IndexedDataset with same indexes, but standardized data
     * @throws IllegalArgumentException if dataset has different dimension from fitted data
     */
    public IndexedDataset transform(IndexedDataset dataset){
        return dataset.copyWithSameIndexes(transform(dataset.getData()));
    }
}
