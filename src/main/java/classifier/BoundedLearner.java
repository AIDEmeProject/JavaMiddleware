package classifier;

import data.LabeledDataset;

public interface BoundedLearner extends Learner {
    BoundedClassifier fit(LabeledDataset data);
}
