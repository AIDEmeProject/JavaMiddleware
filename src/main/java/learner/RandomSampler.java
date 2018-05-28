package learner;

import classifier.Classifier;
import data.LabeledData;

/**
 * The RandomSampler is the most used baseline learner. Basically, at every iteration it randomly samples one point from
 * the unlabeled set at random. It should only be used for comparison with new algorithms, never in any serious settings.
 *
 * @author luciano
 */
public class RandomSampler implements Learner {

    /**
     * A classifier used for fitting and predicting labels
     */
    private Classifier classifier;

    /**
     * @param classifier: classifier used for training and prediction. RandomSampler has no default classification algorithm.
     */
    public RandomSampler(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public void fit(LabeledData data) {
        classifier.fit(data);
    }

    @Override
    public double probability(LabeledData data, int row) {
        return classifier.probability(data, row);
    }

    /**
     * Randomly pick a point from the unlabeled set.
     * @param data: labeled data object
     * @return random unlabeled point index
     */
    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        // TODO: this can be slow if nearly all points have been labeled. Random sample directly from unlabeled points
        while(true){
            int randomIndex = (int)(Math.random() * data.getNumRows());
            if(!data.isInLabeledSet(randomIndex)){
                return randomIndex;
            }
        }
    }
}
