package learner;

import classifier.Classifier;
import data.LabeledData;
import exceptions.EmptyUnlabeledSetException;

/**
 * Uncertainty Sampling is the most common Active Learning technique. Basically, it ranks points through the predicted class
 * probabilities: the closed to 0.5, the most uncertain the current model is about the data point, thus knowing its label
 * should bring a high amount of information.
 *
 * @author luciano
 */
public class UncertaintySampler implements Learner {

    /**
     * classifier used for fitting and predicting labels
     */
    private Classifier classifier;


    public UncertaintySampler(Classifier classifier) {
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
     * Pick the unlabeled data point whose estimated probability is the closest to 0.5.
     * @param data: labeled data object
     * @return most informative point in unlabeled set
     */
    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        if (data.getNumUnlabeledRows() == 0){
            throw new EmptyUnlabeledSetException();
        }

        double minScore = Double.POSITIVE_INFINITY;
        int minRow = -1;

        // TODO: maybe its better to create a single iterator over unlabeled points (index, x[index], y[index]) ?
        for(int i=0; i < data.getNumRows(); i++){
            if(data.isInLabeledSet(i)){
                continue;
            }

            double score = Math.abs(classifier.probability(data, i) - 0.5) ;
            if(score < minScore){
                minScore = score;
                minRow = i;
            }
        }

        return minRow;
    }
}
