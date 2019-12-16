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

package explore;

import config.ExperimentConfiguration;
import data.DataPoint;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.user.User;
import explore.user.UserLabel;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.threesetmetric.ExtendedLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a template for a typical iteration of the Active Learning exploration phase. It consists of 3 main operations:
 *
 *   1) The Active Learning algorithm selects a new collection of points for labeling
 *   2) These points are shown to the user, which provides a POSITIVE / NEGATIVE label for each one
 *   3) Our internal models and data structures are updated with these new labeled data
 *
 * As output, we return a few metrics collected during the exploration process:
 *
 *   - Time measurements: the per-iteration time, and how much time was spent on each one of the operations above
 *   - Labeled points: the new labeled points obtained
 *   - Ranker: the new Ranker object trained by the Active Learning algorithm
 */
public abstract class Iteration {

    /**
     * The {@link ActiveLearner} object used for selecting a new point for label
     */
    private final ActiveLearner activeLearner;

    /**
     * @param configuration an {@link ExperimentConfiguration} object containing all objects necessary for running an iteration
     */
    public Iteration(ExperimentConfiguration configuration) {
        this.activeLearner = configuration.getActiveLearner();
    }

    /**
     * Run a single iteration of the Active Learning exploration process.
     * @param partitionedDataset: the current dataset partition
     * @param user: a {@link User} object
     * @param ranker: a {@link Ranker} obtained through training
     * @return a {@link Result} object containing several metrics collected during exploration
     */
    public Iteration.Result run(PartitionedDataset partitionedDataset, User user, Ranker ranker) {
        Map<String, Double> timeMeasurements = new HashMap<>();
        long initialTime, start = System.nanoTime();

        // find next points to label
        initialTime = System.nanoTime();
        List<DataPoint> mostInformativePoints = getNextPointsToLabel(partitionedDataset, user, ranker);
        timeMeasurements.put("GetNextTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // ask user for labels
        initialTime = System.nanoTime();
        List<LabeledPoint> labeledPoints = getDataPointsLabels(partitionedDataset, user, mostInformativePoints);
        timeMeasurements.put("UserTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // update labeled / unlabeled partitions
        initialTime = System.nanoTime();
        partitionedDataset.update(labeledPoints);
        timeMeasurements.put("UpdatePartitionsTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // update model
        initialTime = System.nanoTime();
        ranker = activeLearner.fit(partitionedDataset.getLabeledPoints());
        timeMeasurements.put("FitTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // iter time
        timeMeasurements.put("IterTimeMillis", (System.nanoTime() - start) / 1e6);

        return new Iteration.Result(timeMeasurements, labeledPoints, ranker);
    }

    /**
     * A data object containing all metrics collected during the exploration process
     */
    public static class Result {
        private final Map<String, Double> timeMeasurements;
        private final List<LabeledPoint> labeledPoints;
        private final Ranker ranker;

        private Result(Map<String, Double> timeMeasurements, List<LabeledPoint> labeledPoints, Ranker ranker) {
            this.timeMeasurements = timeMeasurements;
            this.labeledPoints = labeledPoints;
            this.ranker = ranker;
        }

        public Map<String, Double> getTimeMeasurements() {
            return timeMeasurements;
        }

        public List<LabeledPoint> getLabeledPoints() {
            return labeledPoints;
        }

        public Ranker getRanker() {
            return ranker;
        }
    }

    private List<LabeledPoint> getDataPointsLabels(PartitionedDataset partitionedDataset, User user, List<DataPoint> mostInformativePoints) {
        List<LabeledPoint> labeledPoints = new ArrayList<>();
        for (DataPoint dataPoint : mostInformativePoints) {
            ExtendedLabel extendedLabel = partitionedDataset.getLabel(dataPoint);
            UserLabel label = extendedLabel.isUnknown() ? user.getLabel(dataPoint) : extendedLabel.toLabel();
            labeledPoints.add(new LabeledPoint(dataPoint, label));
        }
        return labeledPoints;
    }

    abstract List<DataPoint> getNextPointsToLabel(PartitionedDataset partitionedDataset, User user, Ranker ranker);
}
