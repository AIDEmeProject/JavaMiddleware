package classifier;

import data.LabeledData;

public interface Learner {
    /**
     * Train its classification model over training data. Only the labeled points should be considered for training.
     * @param data: collection of labeled points
     * @throws exceptions.EmptyUnlabeledSetException if labeled set is empty
     */
    Classifier fit(LabeledData data);

}
