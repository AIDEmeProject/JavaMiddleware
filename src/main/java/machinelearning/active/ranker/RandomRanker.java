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

package machinelearning.active.ranker;

import data.DataPoint;
import data.IndexedDataset;
import machinelearning.active.Ranker;
import utils.RandomState;
import utils.linalg.Vector;

import java.util.Random;
import java.util.stream.IntStream;

public class RandomRanker implements Ranker {

    private Random rnd = RandomState.newInstance();

    @Override
    public Vector score(IndexedDataset unlabeledData) {
        int size = unlabeledData.length();
        double[] indexes = new double[size];

        for (int i = 0; i < size; i++) {
            indexes[i] = (double) i;
        }

        for (int i = size; i > 1; i--) {
            swap(indexes, i - 1, rnd.nextInt(i));
        }

        return Vector.FACTORY.make(indexes);
    }

    private static void swap(double[] arr, int i, int j) {
        double tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    /**
     * @return a random a point from the input collection
     */
    @Override
    public DataPoint top(IndexedDataset unlabeledSet) {
        return unlabeledSet.sample(1).get(0);
    }
}
