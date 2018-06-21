package explore;

import active.ActiveLearner;
import classifier.Classifier;
import data.DataPoint;
import data.LabeledDataset;
import metrics.MetricCalculator;
import sampling.ReservoirSampler;
import sampling.StratifiedSampler;
import user.User;
import utils.Validator;

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

    /**
     * Size of unlabeled set sample taken at every iteration
     */
    private final int subsampleSize;

    /**
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     * @param subsampleSize: size of sample to restrict unlabeled set at every iteration (speeds up computation)
     * @param metricCalculators: collection of metrics to be calculated
     * @throws IllegalArgumentException if budget is not positive
     */
    public Explore(StratifiedSampler initialSampler, int budget, int subsampleSize, Collection<MetricCalculator> metricCalculators) {
        Validator.assertPositive(budget);

        this.initialSampler = initialSampler;
        this.budget = budget;
        this.metricCalculators = metricCalculators;
        this.subsampleSize = subsampleSize;
    }

    /**
     * Through this constructor, no subsampling is performed.
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     * @param metricCalculators: collection of metrics to be calculated
     * @throws IllegalArgumentException if budget is not positive
     */
    public Explore(StratifiedSampler initialSampler, int budget, Collection<MetricCalculator> metricCalculators) {
        this(initialSampler, budget, Integer.MAX_VALUE, metricCalculators);
    }

    /**
     * Through this constructor, no subsampling is performed and no additional metrics will be computed.
     * Only time measurements and the set of labeled rows will be provided in result.
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     */
    public Explore(StratifiedSampler initialSampler, int budget) {
        this(initialSampler, budget, Integer.MAX_VALUE, new ArrayList<>());
    }

    /**
     * Run the exploration process several times (with specified seeds) and average resulting metrics.
     * @param data: data points
     * @param user: user or oracle
     * @param activeLearner: active activeLearner object
     * @param runs: number of runs to perform
     * @return ExplorationMetrics object containing the average value of each metrics of all runs.
     * @throws IllegalArgumentException if runs is not positive and if seeds.length differs from runs
     */
    public ExplorationMetrics run(Collection<DataPoint> data, User user, ActiveLearner activeLearner, int runs, long[] seeds){
        Validator.assertPositive(runs);

        if (seeds != null){
            Validator.assertEquals(runs, seeds.length);
        }

        // initializes active learners internal data structures
        activeLearner.initialize(data);

        // run exploration and compute average of metrics
        ExplorationMetrics metrics = run(data, user, activeLearner, seeds == null ? System.nanoTime() : seeds[0]);

        for (int i = 1; i < runs; i++) {
            metrics = metrics.sum(run(data, user, activeLearner, seeds == null ? System.nanoTime() : seeds[i]));
        }

        return metrics.divideByNumber(runs);
    }

    /**
     * Run the exploration process several times (with random seeds) and average resulting metrics.
     * @param data: data points
     * @param user: user or oracle
     * @param activeLearner: active activeLearner object
     * @param runs: number of runs to perform
     * @return ExplorationMetrics object containing the average value of each metrics of all runs.
     * @throws IllegalArgumentException if runs is not positive
     */
    public ExplorationMetrics run(Collection<DataPoint> data, User user, ActiveLearner activeLearner, int runs){
        return run(data, user, activeLearner, runs, null);
    }

    /**
     * Runs a single exploration process.
     */
    private ExplorationMetrics run(Collection<DataPoint> data, User user, ActiveLearner activeLearner, long seed){
        // set random seed
        setSeed(seed);

        ExplorationMetrics metrics = new ExplorationMetrics();
        LabeledDataset labeledDataset = new LabeledDataset(data);

        for (int iter = 0; iter < budget && labeledDataset.getNumUnlabeledPoints() > 0; iter++){
            metrics.add(runSingleIteration(labeledDataset, user, activeLearner));
        }

        return metrics;
    }

    /**
     * Run a single iteration of the exploration process, computing the necessary metrics.
     */
    private Metrics runSingleIteration(LabeledDataset data, User user, ActiveLearner activeLearner){
        long initialTime;
        Metrics metrics = new Metrics();

        // find next points to label
        initialTime = System.nanoTime();
        Collection<DataPoint> points = getNextPointToLabel(data, user, activeLearner);
        metrics.add("getNextTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // update labeled set
        int[] labels = user.getLabel(points);
        data.putOnLabeledSet(points, labels);
        metrics.add("labeledRow", (double) points.iterator().next().getRow());  //TODO: how to store rows ?

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

    /**
     * Retrieves the next points to labeled, either through the initial sampling or using the active learning model.
     */
    private Collection<DataPoint> getNextPointToLabel(LabeledDataset data, User user, ActiveLearner activeLearner){
        ArrayList<DataPoint> result = new ArrayList<>();

        // initial sampling
        if (data.getNumLabeledPoints() == 0){
            result.addAll(initialSampler.sample(data.getUnlabeledPoints(), user));
        }
        // retrieve most informative point according to model
        else{
            LabeledDataset sample = data.subsampleUnlabeledSet(subsampleSize);
            result.add(activeLearner.retrieveMostInformativeUnlabeledPoint(sample));
        }

        return result;
    }

    /**
     * Sets the random seed for reproducibility.
     * @param seed: random seed value
     */
    private void setSeed(long seed){
        ReservoirSampler.setSeed(seed);
    }
}
