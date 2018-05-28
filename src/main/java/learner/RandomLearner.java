package learner;

import classifier.Classifier;
import data.LabeledData;

public class RandomLearner implements Learner {
    private Classifier classifier;

    public RandomLearner(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public void fit(LabeledData data) {
        classifier.fit(data);
    }

    @Override
    //TODO: rank function has no meaning to RandomLearner... Change design ?
    public double rank(LabeledData data, int rowNumber) {
        return -1;
    }

    @Override
    public int getNext(LabeledData data) {
        // TODO: this can be slow if nearly all points have been labeled. Random sample directly from unlabeled points
        while(true){
            int randomIndex = (int)(Math.random() * data.getNumRows());
            if(!data.rowIsLabeled(randomIndex)){
                return randomIndex;
            }
        }
    }
}
