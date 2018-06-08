package active;

import classifier.Classifier;
import data.LabeledData;
import explore.Metrics;

/**
 * This decorator is used for retrieving timing information for several methods of a typical ActiveLearner object: fit, predict,
 * and retrieveMostInformativeUnlabeledPoint.
 */
public class TimedActiveLearner implements ActiveLearner {

    private ActiveLearner activeLearner;
    private Metrics metrics;

    public TimedActiveLearner(ActiveLearner activeLearner, Metrics metrics) {
        this.activeLearner = activeLearner;
        this.metrics = metrics;
    }

    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        long initialInstant = System.nanoTime();
        int row = activeLearner.retrieveMostInformativeUnlabeledPoint(data);
        metrics.add("retrieveMostInformativeUnlabeledPointTimeMillis", (System.nanoTime() - initialInstant) / 100000.0);
        return row;
    }

    @Override
    public Classifier fit(LabeledData data) {
        long initialInstant = System.nanoTime();
        Classifier classifier = activeLearner.fit(data);
        metrics.add("fitTimeMillis", (System.nanoTime() - initialInstant) / 100000.0);
        return classifier;
    }

//    @Override
//    public int[] predict(LabeledData data) {
//        long initialInstant = System.nanoTime();
//        int[] prediction = activeLearner.predict(data);
//        metrics.add("predictTimeMillis", (System.nanoTime() - initialInstant) / 100000.0);
//        return prediction;
//    }
//
//    @Override
//    public double probability(LabeledData data, int row) {
//        return activeLearner.probability(data, row);
//    }
//
//    @Override
//    public int predict(LabeledData data, int row) {
//        return activeLearner.predict(data, row);
//    }
}
