package explore;

import data.LabeledData;
import learner.Learner;
import learner.TimedLearner;
import metrics.ConfusionMatrix;
import sampling.ReservoirSampler;
import sampling.StratifiedSampler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This module is responsible for running the Active Learning exploration process. This is an iterative process which
 * performs the following operations every iteration:
 *
 *  1) Retrieve the next point to label
 *  2) Label is retrieved and labeled set is updated
 *  3) We retrain our model over the current labeled set of points
 *
 * This process is repeated during a pre-defined number of operations.
 */
public class Explore {
    /**
     * Number of iteration to run in the active learning exploration process.
     */
    private int budget;

    /**
     * Initial sampler. It randomly chooses an initial batch of positive and negative points to be labeled.
     */
    private StratifiedSampler initialSampler;

    /**
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     * @throws NullPointerException if initialSampler is null
     * @throws IllegalArgumentException if budget is not positive
     */
    public Explore(StratifiedSampler initialSampler, int budget) {
        if (initialSampler == null){
            throw new NullPointerException("Initial Sampler cannot be null.");
        }

        if (budget <= 0){
            throw new IllegalArgumentException("Budget must be a positive number.");
        }

        this.initialSampler = initialSampler;
        this.budget = budget;
    }

    private void setSeed(long seed){
        ReservoirSampler.setSeed(seed);
    }

    /**
     * Run the exploration process.
     * @param X: features matrix
     * @param y: labels array
     * @param learner: active learner object
     * @param seed: random seed to be used throughout exploration. Allows experiments to be reproducible.
     * @return metrics collected during each iteration.
     */
    public List<Map<String, Double>> run(double[][] X, int[] y, Learner learner, long seed){
        // set random seed
        setSeed(seed);

        // TODO: maybe we should pass the labeledData instance directly as parameter ?
        List<Map<String, Double>> metrics = new ArrayList<>();
        LabeledData data = new LabeledData(X, y);

        for (int iter = 0; iter < budget && data.getNumUnlabeledRows() > 0; iter++){
            metrics.add(runSingleIteration(data, learner));
        }

        // TODO: return labeledData object or labeled rows indexes only?
        return metrics;
    }

    /**
     * Run the exploration process with a random seed.
     * @param X: features matrix
     * @param y: labels array
     * @param learner: active learner object
     */
    public List<Map<String, Double>> run(double[][] X, int[] y, Learner learner){
        return run(X, y, learner, System.currentTimeMillis());
    }

    private Map<String, Double> runSingleIteration(LabeledData data, Learner learner){
        Map<String, Double> metrics = new HashMap<>();
        learner = new TimedLearner(learner, metrics);  // Apply timing decorator

        // find next points to label
        int[] rows = getNextPointToLabel(data, learner);

        // update labeled set
        data.addLabeledRow(rows);
        metrics.put("labeledRow", (double) rows[0]);

        // retrain model
        learner.fit(data);

        // compute accuracy metrics
        ConfusionMatrix confusionMatrix = ConfusionMatrix.compute(data.getY(), learner.predict(data));
        metrics.putAll(confusionMatrix.getMetrics());

        return metrics;
    }

    private int[] getNextPointToLabel(LabeledData data, Learner learner){
        // initial sampling
        if (data.getNumLabeledRows() == 0){
            return initialSampler.sample(data.getY());
        }

        // retrieve most informative point according to model
        return new int[] {learner.retrieveMostInformativeUnlabeledPoint(data)};
    }
}
