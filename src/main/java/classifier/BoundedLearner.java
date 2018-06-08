package classifier;

import data.LabeledData;

public interface BoundedLearner extends Learner {
    BoundedClassifier fit(LabeledData data);
}
