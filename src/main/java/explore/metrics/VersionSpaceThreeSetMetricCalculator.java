package explore.metrics;

import data.PartitionedDataset;
import explore.user.User;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.MajorityVoteLearner;
import utils.linalg.Vector;

public class VersionSpaceThreeSetMetricCalculator implements MetricCalculator {

    private MajorityVoteLearner learner;
    private double margin;

    @Override
    public MetricStorage compute(PartitionedDataset data, User user) {
        Classifier majorityVoteClassifier = learner.fit(data.getLabeledPoints());

        Vector cutProbabilities = majorityVoteClassifier.probability(data.getAllPoints());

        double positiveCount = 0D, uncertainCount = 0D;
        for (int i = 0; i < cutProbabilities.dim(); i++) {
            double p = cutProbabilities.get(i);

            if (p >= 1 - margin) {
                positiveCount++;
            } else if (p > margin) {
                uncertainCount++;
            }
        }
        System.out.println("lower bound: " + positiveCount / (positiveCount + uncertainCount));
        return new ThreeSetMetric(positiveCount, uncertainCount);
    }
}
