package active;

import classifier.Learner;
import data.DataPoint;
import data.LabeledDataset;

import java.util.Collection;

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
     * @throws IllegalArgumentException if unlabeled set is empty
     */
    DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data);
}
