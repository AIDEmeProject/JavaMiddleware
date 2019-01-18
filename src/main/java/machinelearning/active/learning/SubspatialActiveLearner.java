package machinelearning.active.learning;

import data.LabeledDataset;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.SubspatialRanker;
import utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * A Subspatial Active Learner decomposes the learning task across each feature subspace. Basically, one particular
 * Active Learner is fit over each data subspace using the partial label information, and a final {@link Ranker}
 * object returned which pieces together all subspace {@link Ranker} objects together.
 */
public class SubspatialActiveLearner implements ActiveLearner {

    /**
     * Active Learners to be fit to each subspace
     */
    private final ActiveLearner[] activeLearners;

    /**
     * @throws IllegalArgumentException if input arrays have incompatible sizes or are empty
     */
    public SubspatialActiveLearner(ActiveLearner[] activeLearners) {
        Validator.assertNotEmpty(activeLearners);

        this.activeLearners = activeLearners;
    }

    @Override
    public Ranker fit(LabeledDataset labeledPoints) {
        LabeledDataset[] partitionedData = labeledPoints.getPartitionedData();
        int size = partitionedData.length;

        // create list of tasks to be run
        List<Callable<Ranker>> workers = new ArrayList<>(size);

        for(int i = 0; i < size; i++){
            workers.add(new ActiveLearnerWorker(partitionedData[i], activeLearners[i]));
        }

        try {
            // execute all tasks
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(size, Runtime.getRuntime().availableProcessors() - 1));
            List<Future<Ranker>> subspaceRankersFuture = executor.invokeAll(workers);
            executor.shutdownNow();

            // parse result
            Ranker[] subspaceRankers = new Ranker[size];

            for (int i = 0; i < size; i++) {
                subspaceRankers[i] = subspaceRankersFuture.get(i).get();
            }

            return new SubspatialRanker(subspaceRankers);
        } catch (InterruptedException ex) {
            throw new RuntimeException("Thread was abruptly interrupted.", ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException("Exception thrown at running task.", ex);
        }
    }

    /**
     * Helper class for multi-threaded fit() method
     */
    private static class ActiveLearnerWorker implements Callable<Ranker> {

        private final LabeledDataset labeledData;
        private final ActiveLearner activeLearner;

        public ActiveLearnerWorker(LabeledDataset labeledData, ActiveLearner activeLearner) {
            this.labeledData = labeledData;
            this.activeLearner = activeLearner;
        }

        @Override
        public Ranker call() {
            return activeLearner.fit(labeledData);
        }
    }
}
