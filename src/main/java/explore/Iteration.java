package explore;

import data.DataPoint;
import data.LabeledPoint;
import data.PartitionedDataset;
import explore.user.User;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.ExtendedLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Iteration {

    private final ActiveLearner activeLearner;

    public Iteration(ExperimentConfiguration configuration) {
        this.activeLearner = configuration.getActiveLearner();
    }

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
            Label label = extendedLabel == ExtendedLabel.UNKNOWN ? user.getLabel(dataPoint) : extendedLabel.toLabel();
            labeledPoints.add(new LabeledPoint(dataPoint, label));
        }
        return labeledPoints;
    }

    abstract List<DataPoint> getNextPointsToLabel(PartitionedDataset partitionedDataset, User user, Ranker ranker);
}
