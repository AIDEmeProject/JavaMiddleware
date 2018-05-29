package learner;

import classifier.Classifier;
import data.LabeledData;

/**
 * Learner represents a classifier adapted to the Active Learning scenario. It is augmented with the capacity of
 * "ranking unlabeled points from least to most informative", retrieving the one it deems most informative for labeling.
 *
 * @author luciano
 */
public interface Learner extends Classifier{
    /**
     * Retrieve the most informative point for labeling from the unlabeled set.
     * @param data: labeled data object
     * @return index of the next unlabeled point to label
     * @throws exceptions.EmptyUnlabeledSetException if unlabeled set is empty
     */
    int retrieveMostInformativeUnlabeledPoint(LabeledData data);
}
