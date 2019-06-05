package explore.metrics;

import data.PartitionedDataset;
import explore.user.User;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.MajorityVoteLearner;
import utils.linalg.Vector;

public class VersionSpaceLowerBoundCalculator implements MetricCalculator {

    private MajorityVoteLearner learner;
    private double margin;

    @Override
    public MetricStorage compute(PartitionedDataset data, User user) {
        Classifier majorityVoteClassifier = learner.fit(data.getLabeledPoints());

        Vector cutProbabilities = majorityVoteClassifier.probability(data.getAllPoints());

        int size = cutProbabilities.dim();
        int positiveCount = 0, negativeCount = 0, predictedPositives = 0;
        for (int i = 0; i < size; i++) {
            double p = cutProbabilities.get(i);

            if (p >= 1 - margin) {
                positiveCount++;
            } else if (p <= margin) {
                negativeCount++;
            }

            if (p >= 0.5) predictedPositives++;
        }

        return new VersionSpaceLowerBound(positiveCount, negativeCount, predictedPositives, size);
    }
}
