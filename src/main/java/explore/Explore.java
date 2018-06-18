package explore;

import active.ActiveLearner;
import classifier.Classifier;
import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import metrics.MetricCalculator;
import sampling.ReservoirSampler;
import sampling.StratifiedSampler;
import user.User;

import java.util.ArrayList;
import java.util.Collection;


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

    private final int subsampleSize;

    /**
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     * @param metricCalculators: collection of metrics to be calculated
     * @throws NullPointerException if initialSampler is null
     * @throws IllegalArgumentException if budget is not positive
     */
    public Explore(StratifiedSampler initialSampler, int budget, int subsampleSize, Collection<MetricCalculator> metricCalculators) {
        if (initialSampler == null){
            throw new NullPointerException("Initial Sampler cannot be null.");
        }

        if (budget <= 0){
            throw new IllegalArgumentException("Budget must be a positive number.");
        }

        this.initialSampler = initialSampler;
        this.budget = budget;
        this.metricCalculators = metricCalculators;
        this.subsampleSize = subsampleSize;
    }

    /**
     * Sets no metrics to calculate. Only time measurements will be provided in result.
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     */
    public Explore(StratifiedSampler initialSampler, int budget) {
        this(initialSampler, budget, Integer.MAX_VALUE, new ArrayList<>());
    }

    public Explore(StratifiedSampler initialSampler, int budget, Collection<MetricCalculator> metricCalculators) {
        this(initialSampler, budget, Integer.MAX_VALUE, metricCalculators);
    }

    private void setSeed(long seed){
        ReservoirSampler.setSeed(seed);
    }

    /**
     * Run the exploration process.
     * @param X: features matrix
     * @param user: user or oracle
     * @param activeLearner: active activeLearner object
     * @param seed: random seed to be used throughout exploration. Allows experiments to be reproducible.
     * @return metrics collected during each iteration.
     */
    public ExplorationMetrics run(double[][] X, User user, ActiveLearner activeLearner, long seed){
        // set random seed
        setSeed(seed);

        ExplorationMetrics metrics = new ExplorationMetrics();
        LabeledDataset data = new LabeledDataset(X); // TODO: maybe we should pass the labeledData instance directly as parameter ?

        for (int iter = 0; iter < budget && data.getNumUnlabeledRows() > 0; iter++){
            metrics.add(runSingleIteration(data, user, activeLearner));
        }

        return metrics;
    }

    /**
     * Run the exploration process with a random seed.
     * @param X: features matrix
     * @param user: user or oracle
     * @param activeLearner: active activeLearner object
     */
    public ExplorationMetrics run(double[][] X, User user, ActiveLearner activeLearner){
        return run(X, user, activeLearner, System.nanoTime());
    }

    /**
     * Run the exploration process several times (with specified seeds) and average resulting metrics.
     * @param X: features matrix
     * @param user: user or oracle
     * @param activeLearner: active activeLearner object
     * @param runs: number of runs to perform
     * @return ExplorationMetrics object containing the average value of each metrics of all runs.
     */
    public ExplorationMetrics averageRun(double[][] X, User user, ActiveLearner activeLearner, int runs, long[] seeds){
        if (runs <= 0){
            throw new IllegalArgumentException("Runs must be positive.");
        }

        ExplorationMetrics metrics = run(X, user, activeLearner, seeds[0]);

        for (int i = 1; i < runs; i++) {
            metrics = metrics.sum(run(X, user, activeLearner, seeds[i]));
        }

        return metrics.divideByNumber(runs);
    }

    /**
     * Run the exploration process several times (with random seeds) and average resulting metrics.
     * @param X: features matrix
     * @param user: user or oracle
     * @param activeLearner: active activeLearner object
     * @param runs: number of runs to perform
     * @return ExplorationMetrics object containing the average value of each metrics of all runs.
     * TODO: can we remove the duplication between this method and other averageRun? (i.e. how to choose "random" seeds?)
     */
    public ExplorationMetrics averageRun(double[][] X, User user, ActiveLearner activeLearner, int runs){
        if (runs <= 0){
            throw new IllegalArgumentException("Runs must be positive.");
        }

        ExplorationMetrics metrics = run(X, user, activeLearner);

        for (int i = 1; i < runs; i++) {
            metrics = metrics.sum(run(X, user, activeLearner));
        }

        return metrics.divideByNumber(runs);
    }

    private Metrics runSingleIteration(LabeledDataset data, User user, ActiveLearner activeLearner){
        long initialTime;
        Metrics metrics = new Metrics();

        // find next points to label
        initialTime = System.nanoTime();
        Collection<DataPoint> points = getNextPointToLabel(data, user, activeLearner);
        metrics.add("getNextTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // update labeled set
        int[] labels = user.getLabel(points);
        data.addLabeledRow(points, labels);
        metrics.add("labeledRow", (double) points.iterator().next().getId());  //TODO: how to store rows ?

        // retrain model
        initialTime = System.nanoTime();
        Classifier classifier = activeLearner.fit(data.getLabeledPoints());
        metrics.add("fitTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // compute accuracy metrics
        initialTime = System.nanoTime();
        for (MetricCalculator metricCalculator : metricCalculators){
            metrics.addAll(metricCalculator.compute(data, user, classifier).getMetrics());
        }
        metrics.add("accuracyComputationTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        return metrics;
    }

    private Collection<DataPoint> getNextPointToLabel(LabeledDataset data, User user, ActiveLearner activeLearner){
        int[] rows = data.getNumLabeledRows() == 0 ?
                     initialSampler.sample(user.getLabel(data.getUnlabeledPoints())) :
                     new int[] {activeLearner.retrieveMostInformativeUnlabeledPoint(data.subsampleUnlabeledSet(subsampleSize))};

        Collection<DataPoint> points = new ArrayList<>(rows.length);
        for (int row : rows){
            points.add(data.getRow(row));
        }

        return points;
    }
}
