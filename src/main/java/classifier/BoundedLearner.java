package classifier;

import data.LabeledData;

public interface BoundedLearner {
    BoundedClassifier fit(LabeledData data);
}
