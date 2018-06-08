package explore;

import classifier.Classifier;
import data.LabeledData;
import active.ActiveLearner;
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
    private final int budget;

    /**
     * Initial sampler. It randomly chooses an initial batch of positive and negative points to be labeled.
     */
    private final StratifiedSampler initialSampler;

    /**
     * metric calculators
     */
    private final Collection<MetricCalculator> metricCalculators;

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
     * @param activeLearner: active activeLearner object
     * @param seed: random seed to be used throughout exploration. Allows experiments to be reproducible.
     * @return metrics collected during each iteration.
     */
    public ExplorationMetrics run(double[][] X, int[] y, ActiveLearner activeLearner, long seed){
        // set random seed
        setSeed(seed);

        ExplorationMetrics metrics = new ExplorationMetrics();
        LabeledData data = new LabeledData(X, y); // TODO: maybe we should pass the labeledData instance directly as parameter ?

        for (int iter = 0; iter < budget && data.getNumUnlabeledRows() > 0; iter++){
            metrics.add(runSingleIteration(data, activeLearner));
        }

        return metrics;
    }

    /**
     * Run the exploration process with a random seed.
     * @param X: features matrix
     * @param y: labels array
     * @param activeLearner: active activeLearner object
     */
    public ExplorationMetrics run(double[][] X, int[] y, ActiveLearner activeLearner){
        return run(X, y, activeLearner, System.nanoTime());
    }

    /**
     * Run the exploration process several times (with specified seeds) and average resulting metrics.
     * @param X: features matrix
     * @param y: labels array
     * @param activeLearner: active activeLearner object
     * @param runs: number of runs to perform
     * @return ExplorationMetrics object containing the average value of each metrics of all runs.
     */
    public ExplorationMetrics averageRun(double[][] X, int[] y, ActiveLearner activeLearner, int runs, long[] seeds){
        if (runs <= 0){
            throw new IllegalArgumentException("Runs must be positive.");
        }

        ExplorationMetrics metrics = run(X, y, activeLearner, seeds[0]);

        for (int i = 1; i < runs; i++) {
            metrics = metrics.sum(run(X, y, activeLearner, seeds[i]));
        }

        return metrics.divideByNumber(runs);
    }

    /**
     * Run the exploration process several times (with random seeds) and average resulting metrics.
     * @param X: features matrix
     * @param y: labels array
     * @param activeLearner: active activeLearner object
     * @param runs: number of runs to perform
     * @return ExplorationMetrics object containing the average value of each metrics of all runs.
     * TODO: can we remove the duplication between this method and other averageRun? (i.e. how to choose "random" seeds?)
     */
    public ExplorationMetrics averageRun(double[][] X, int[] y, ActiveLearner activeLearner, int runs){
        if (runs <= 0){
            throw new IllegalArgumentException("Runs must be positive.");
        }

        ExplorationMetrics metrics = run(X, y, activeLearner);

        for (int i = 1; i < runs; i++) {
            metrics = metrics.sum(run(X, y, activeLearner));
        }

        return metrics.divideByNumber(runs);
    }

    private Metrics runSingleIteration(LabeledData data, ActiveLearner activeLearner){
        long initialTime;
        Metrics metrics = new Metrics();

        // find next points to label
        initialTime = System.nanoTime();
        int[] rows = getNextPointToLabel(data, activeLearner);
        metrics.add("getNextTimeMillis", (System.nanoTime() - initialTime) / 1000000.);

        // update labeled set
        data.addLabeledRow(rows);
        metrics.add("labeledRow", (double) rows[0]);  //TODO: how to store rows ?

        // retrain model
        initialTime = System.nanoTime();
        Classifier classifier = activeLearner.fit(data);
        metrics.add("fitTimeMillis", (System.nanoTime() - initialTime) / 1000000.);

        // compute accuracy metrics
        initialTime = System.nanoTime();
        for (MetricCalculator metricCalculator : metricCalculators){
            metrics.addAll(metricCalculator.compute(data, classifier).getMetrics());
        }
        metrics.add("accuracyComputationTimeMillis", (System.nanoTime() - initialTime) / 1000000.);

        return metrics;
    }

    private int[] getNextPointToLabel(LabeledData data, ActiveLearner activeLearner){
        // initial sampling
        if (data.getNumLabeledRows() == 0){
            return initialSampler.sample(data.getY());
        }

        // retrieve most informative point according to model
        return new int[] {activeLearner.retrieveMostInformativeUnlabeledPoint(data)};
    }
}
