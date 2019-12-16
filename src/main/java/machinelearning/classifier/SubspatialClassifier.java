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

import data.IndexedDataset;
import utils.Validator;
import utils.linalg.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * A classifier leveraging subspatial decomposition information.
 */
public class SubspatialClassifier implements Classifier {
    private final int[][] partitionIndexes;
    private final Classifier[] subspaceClassifiers;
    private final SubspatialWorker worker;

    public SubspatialClassifier(int[][] partitionIndexes, Classifier[] subspaceClassifiers, SubspatialWorker worker) {
        Validator.assertNotEmpty(partitionIndexes);
        Validator.assertEqualLengths(partitionIndexes, subspaceClassifiers);

        this.partitionIndexes = partitionIndexes;
        this.subspaceClassifiers = subspaceClassifiers;
        this.worker = worker;
    }

    @Override
    public double probability(Vector vector) {
        double minProbability = 1, probability;

        for (int i=0; i < partitionIndexes.length; i++) {
            probability = subspaceClassifiers[i].probability(vector.select(partitionIndexes[i]));
            if(probability < minProbability) {
                minProbability = probability;
            }
        }

        return minProbability;
    }

    @Override
    public Vector probability(IndexedDataset dataset) {
        Validator.assertEquals(dataset.getPartitionIndexes(), partitionIndexes);

        Vector[] subspaceProbabilities = probabilityAllSubspaces(dataset);
        Vector probability = subspaceProbabilities[0];

        for (int i = 1; i < subspaceProbabilities.length; i++) {
            for (int j = 0; j < probability.dim(); j++) {
                probability.set(j, Math.min(probability.get(j), subspaceProbabilities[i].get(j)));
            }
        }

        return probability;
    }

    public Vector[] probabilityAllSubspaces(IndexedDataset dataset) {
        IndexedDataset[] partitionedData = dataset.getPartitionedData();

        // create list of tasks to be run
        List<Callable<Vector>> workers = new ArrayList<>();

        for(int i = 0; i < subspaceClassifiers.length; i++){
            workers.add(new ProbabilityWorker(subspaceClassifiers[i], partitionedData[i]));
        }

        return worker.run(workers).toArray(new Vector[0]);
    }

    @Override
    public Label[] predict(IndexedDataset dataset) {
        Label[][] allPredictions = predictAllSubspaces(dataset);

        Label[] finalPredictions = new Label[dataset.length()];
        for (int i = 0; i < dataset.length(); i++) {
            finalPredictions[i] = Label.POSITIVE;

            for (int j = 0; j < dataset.partitionSize(); j++) {
                if (allPredictions[j][i].isNegative()) {
                    finalPredictions[i] = Label.NEGATIVE;
                    break;
                }
            }
        }

        return finalPredictions;
    }

    public Label[][] predictAllSubspaces(IndexedDataset dataset) {
        int size = dataset.partitionSize();
        IndexedDataset[] partitionedDatasets = dataset.getPartitionedData();

        Label[][] allLabels = new Label[size][dataset.length()];
        for (int i = 0; i < size; i++) {
            allLabels[i] = subspaceClassifiers[i].predict(partitionedDatasets[i]);
        }

        return allLabels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubspatialClassifier that = (SubspatialClassifier) o;

        if (partitionIndexes.length != that.partitionIndexes.length)
            return false;

        for (int i = 0; i < partitionIndexes.length; i++) {
            if (!Arrays.equals(partitionIndexes[i], that.partitionIndexes[i]))
                return false;
        }

        return Arrays.equals(subspaceClassifiers, that.subspaceClassifiers) &&
                Objects.equals(worker, that.worker);
    }

    /**
     * Helper class for multi-threaded score() method
     */
    private static class ProbabilityWorker implements Callable<Vector> {

        private final Classifier classifier;
        private final IndexedDataset unlabeledData;

        ProbabilityWorker(Classifier classifier, IndexedDataset unlabeledData) {
            this.classifier = classifier;
            this.unlabeledData = unlabeledData;
        }

        @Override
        public Vector call() {
            return classifier.probability(unlabeledData);
        }
    }
}
