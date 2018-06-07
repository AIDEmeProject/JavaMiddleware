package explore;

import data.LabeledData;
import learner.Learner;
import learner.TimedLearner;
import metrics.ConfusionMatrix;
import metrics.MetricCalculator;
import sampling.ReservoirSampler;
import sampling.StratifiedSampler;

import java.util.*;


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
     * metric calculators
     */
    private Collection<MetricCalculator> metricCalculators;

    /**
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     * @param metricCalculators: collection of metrics to be calculated
     * @throws NullPointerException if initialSampler is null
     * @throws IllegalArgumentException if budget is not positive
     */
    public Explore(StratifiedSampler initialSampler, int budget, Collection<MetricCalculator> metricCalculators) {
        if (initialSampler == null){
            throw new NullPointerException("Initial Sampler cannot be null.");
        }

        if (budget <= 0){
            throw new IllegalArgumentException("Budget must be a positive number.");
        }

        this.initialSampler = initialSampler;
        this.budget = budget;
        this.metricCalculators = metricCalculators;
    }

    /**
     * Sets no metrics to calculate. Only time measurements will be provided in result.
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     */
    public Explore(StratifiedSampler initialSampler, int budget) {
        this(initialSampler, budget, new ArrayList<>());
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
    public ExplorationMetrics run(double[][] X, int[] y, Learner learner, long seed){
        // set random seed
        setSeed(seed);

        // TODO: maybe we should pass the labeledData instance directly as parameter ?
        List<Map<String, Double>> metrics = new ArrayList<>();
        LabeledData data = new LabeledData(X, y);

        for (int iter = 0; iter < budget && data.getNumUnlabeledRows() > 0; iter++){
            metrics.add(runSingleIteration(data, learner));
        }

        // TODO: return labeledData object or labeled rows indexes only?
        return new ExplorationMetrics(metrics);
    }

    /**
     * Run the exploration process with a random seed.
     * @param X: features matrix
     * @param y: labels array
     * @param learner: active learner object
     */
    public ExplorationMetrics run(double[][] X, int[] y, Learner learner){
        return run(X, y, learner, System.nanoTime());
    }

    /**
     * Run the exploration process several times (with random seeds) and average resulting metrics.
     * @param X: features matrix
     * @param y: labels array
     * @param learner: active learner object
     * @param runs: number of runs to perform
     * @return ExplorationMetrics object containing the average value of each metrics of all runs.
     * TODO: can we remove the duplication between this method and the below? (i.e. how to choose "random" seeds?)
     */
    public ExplorationMetrics averageRun(double[][] X, int[] y, Learner learner, int runs){
        if (runs <= 0){
            throw new IllegalArgumentException("Runs must be positive.");
        }

        ExplorationMetrics metrics = run(X, y, learner);

        for (int i = 1; i < runs; i++) {
            metrics = ExplorationMetrics.sum(metrics, run(X, y, learner));
        }

        return ExplorationMetrics.divideByNumber(metrics, runs);
    }

    /**
     * Run the exploration process several times (with specified seeds) and average resulting metrics.
     * @param X: features matrix
     * @param y: labels array
     * @param learner: active learner object
     * @param runs: number of runs to perform
     * @return ExplorationMetrics object containing the average value of each metrics of all runs.
     */
    public ExplorationMetrics averageRun(double[][] X, int[] y, Learner learner, int runs, long[] seeds){
        if (runs <= 0){
            throw new IllegalArgumentException("Runs must be positive.");
        }

        ExplorationMetrics metrics = run(X, y, learner, seeds[0]);

        for (int i = 1; i < runs; i++) {
            metrics = ExplorationMetrics.sum(metrics, run(X, y, learner, seeds[i]));
        }

        return ExplorationMetrics.divideByNumber(metrics, runs);
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
        for (MetricCalculator metricCalculator : metricCalculators){
            metrics.putAll(metricCalculator.compute(data, learner).getMetrics());
        }

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
