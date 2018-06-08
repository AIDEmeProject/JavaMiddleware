package active;

import classifier.Learner;
import data.LabeledData;

/**
 * ActiveLearner represents a classifier adapted to the Active Learning scenario. It is augmented with the capacity of
 * "ranking unlabeled points from least to most informative", retrieving the one it deems most informative for labeling.
 *
 * @author luciano
 */
public interface ActiveLearner extends Learner{
    /**
     * Retrieve the most informative point for labeling from the unlabeled set.
     * @param data: labeled data object
     * @return index of the next unlabeled point to label
     * @throws exceptions.EmptyUnlabeledSetException if unlabeled set is empty
     * TODO: change return type to int[] or collection of int?
     * TODO: add subsampling for faster computation
     */
    int retrieveMostInformativeUnlabeledPoint(LabeledData data);
}
