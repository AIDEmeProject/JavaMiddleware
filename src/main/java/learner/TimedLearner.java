package learner;

import data.LabeledData;
import explore.Metrics;

/**
 * This decorator is used for retrieving timing information for several methods of a typical Learner object: fit, predict,
 * and retrieveMostInformativeUnlabeledPoint.
 */
public class TimedLearner implements Learner {

    private Learner learner;
    private Metrics metrics;

    public TimedLearner(Learner learner, Metrics metrics) {
        this.learner = learner;
        this.metrics = metrics;
    }

    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        long initialInstant = System.nanoTime();
        int row = learner.retrieveMostInformativeUnlabeledPoint(data);
        metrics.add("retrieveMostInformativeUnlabeledPointTimeMillis", (System.nanoTime() - initialInstant) / 100000.0);
        return row;
    }

    @Override
    public void fit(LabeledData data) {
        long initialInstant = System.nanoTime();
        learner.fit(data);
        metrics.add("fitTimeMillis", (System.nanoTime() - initialInstant) / 100000.0);
    }

    @Override
    public int[] predict(LabeledData data) {
        long initialInstant = System.nanoTime();
        int[] prediction = learner.predict(data);
        metrics.add("predictTimeMillis", (System.nanoTime() - initialInstant) / 100000.0);
        return prediction;
    }

    @Override
    public double probability(LabeledData data, int row) {
        return learner.probability(data, row);
    }

    @Override
    public int predict(LabeledData data, int row) {
        return learner.predict(data, row);
    }
}
