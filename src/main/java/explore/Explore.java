package explore;

import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import explore.sampling.InitialSampler;
import explore.sampling.ReservoirSampler;
import explore.user.User;
import io.FolderManager;
import machinelearning.active.ActiveLearner;
import utils.Validator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This module is responsible for running the Active Learning exploration process. This is an iterative process which
 * performs the following operations every iteration:
 *
 *  1) Retrieve the next point to label
 *  2) Label is retrieved and labeled set is updated
 *  3) We retrain our model over the current labeled set of points
 *
 * This process is repeated during a predefined number of operations.
 */
public class Explore {
    /**
     * Number of iteration to run in the active learning exploration process.
     */
    private final int budget;

    /**
     * Initial sampler. It randomly chooses an initial batch of positive and negative points to be labeled.
     */
    private final InitialSampler initialSampler;

    /**
     * Size of unlabeled set sample taken at every iteration
     */
    private final int subsampleSize;

    /**
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     * @param subsampleSize: size of sample to restrict unlabeled set at every iteration (speeds up computation)
     * @throws IllegalArgumentException if budget is not positive
     */
    public Explore(InitialSampler initialSampler, int budget, int subsampleSize) {
        Validator.assertPositive(budget);
        this.initialSampler = initialSampler;
        this.budget = budget;
        this.subsampleSize = subsampleSize;
    }

    /**
     * Through this constructor, no subsampling is performed.
     * @param initialSampler: initial sampling method. It randomly picks a given number of positive and negative points
     * @param budget: number of iterations in the active learning exploration process
     * @throws IllegalArgumentException if budget is not positive
     */
    public Explore(InitialSampler initialSampler, int budget) {
        this(initialSampler, budget, Integer.MAX_VALUE);
    }
    /**
     * Run the exploration process several times (with random seeds), saving all exploration metrics  to disk.
     * @param data: data points
     * @param user: user or oracle
     * @param activeLearner: active activeLearner object
     * @param runs: number of runs to perform
     * @param folder: folder which results are to be saved
     * @throws IllegalArgumentException if runs is not positive
     */
    public void run(Collection<DataPoint> data, User user, ActiveLearner activeLearner, int runs, FolderManager folder){
        run(data, user, activeLearner, runs, null, folder);
    }

    /**
     * Run the exploration process several times (with specified seeds) and average resulting metrics .
     * @param data: data points
     * @param user: user or oracle
     * @param activeLearner: active activeLearner object
     * @param runs: number of runs to perform
     * @param seeds: random seeds for each run
     * @param folder: folder which results are to be saved
     * @throws IllegalArgumentException if runs is not positive and if seeds.length differs from runs
     */
    public void run(Collection<DataPoint> data, User user, ActiveLearner activeLearner, int runs, long[] seeds, FolderManager folder){
        Validator.assertPositive(runs);
        Validator.assertNotNull(folder);

        if (seeds != null){
            Validator.assertEquals(runs, seeds.length);
        }

        for (int i = 0; i < runs; i++) {
            runSingleExploration(data, user, activeLearner, seeds == null ? System.nanoTime() : seeds[i], folder.createNewRunFile());
        }
    }

    /**
     * Runs a single exploration process. Metrics will be logged to file as soon as each iteration finishes.
     */
    private void runSingleExploration(Collection<DataPoint> data, User user, ActiveLearner activeLearner, long seed, File file){
        // set random seed
        setSeed(seed);

        LabeledDataset labeledDataset = new LabeledDataset(data);

        try (FileWriter fileWriter = new FileWriter(file, true)) {
            BufferedWriter writer = new BufferedWriter(fileWriter);

            for (int iter = 0; iter < budget && labeledDataset.getNumUnlabeledPoints() > 0; iter++) {
                writer.write(runSingleIteration(labeledDataset, user, activeLearner).toString());
                writer.newLine();
                writer.flush();
            }
        }
        catch (IOException ex){
            //TODO: log this error
            ex.printStackTrace();
        }
    }

    /**
     * Run a single iteration of the exploration process, computing the necessary metrics .
     */
    private IterationMetrics runSingleIteration(LabeledDataset data, User user, ActiveLearner activeLearner){
        long initialTime, start;
        IterationMetrics metrics = new IterationMetrics();

        // find next points to label
        initialTime = System.nanoTime();
        start = initialTime;
        Collection<DataPoint> points = getNextPointToLabel(data, user, activeLearner);
        metrics.put("GetNextTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // update labeled set
        initialTime = System.nanoTime();
        Collection<LabeledPoint> labeledPoints = user.getLabeledPoint(points);
        metrics.put("UserTimeMillis", (System.nanoTime() - initialTime) / 1e6);
        metrics.setLabeledPoints(labeledPoints);

        data.putOnLabeledSet(labeledPoints);

        // retrain model
        initialTime = System.nanoTime();
        activeLearner.update(labeledPoints);
        metrics.put("FitTimeMillis", (System.nanoTime() - initialTime) / 1e6);

        // compute accuracy metrics
//        initialTime = System.nanoTime();
//        for (MetricCalculator metricCalculator : metricCalculators){
//            metrics.putAll(metricCalculator.compute(data, user, classifier).getMetrics());
//        }
//        metrics.put("AccuracyComputationTimeMillis",(System.nanoTime() - initialTime) / 1e6);

        metrics.put("IterTimeMillis",(System.nanoTime() - start) / 1e6);

        return metrics;
    }

    /**
     * Retrieves the next points to labeled, either through the initial sampling or using the active learning model.
     */
    private List<DataPoint> getNextPointToLabel(LabeledDataset data, User user, ActiveLearner activeLearner){
        ArrayList<DataPoint> result = new ArrayList<>();

        // initial sampling
        if (data.getNumLabeledPoints() == 0){
            result.addAll(initialSampler.runInitialSample(data.getUnlabeledPoints(), user));
        }
        // retrieve most informative point according to model
        else{
            Collection<DataPoint> sample = ReservoirSampler.sample(data.getUnlabeledPoints(), subsampleSize);
            result.add(activeLearner.getRanker().top(sample));
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
