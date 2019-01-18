package machinelearning.active.ranker;

import data.IndexedDataset;
import machinelearning.active.Ranker;
import utils.Validator;
import utils.linalg.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * A Subspatial Ranker contains a collection of Ranker objects, each one fitted over a particular data subspace.
 * The final score will be computed by summing the partial scores of each subspace. We assume that all partial scores
 * are somewhat "in the same range".
 */
public class SubspatialRanker implements Ranker {
    /**
     * Ranker objects for each subspace
     */
    private final Ranker[] subspaceRankers;

    /**
     * @throws IllegalArgumentException if input arrays have incompatible sizes or are empty
     */
    public SubspatialRanker(Ranker[] subspaceRankers) {
        Validator.assertNotEmpty(subspaceRankers);

        this.subspaceRankers = subspaceRankers;
    }

    @Override
    public Vector score(IndexedDataset unlabeledData) {
        int size = subspaceRankers.length;
        IndexedDataset[] partitionedData = unlabeledData.getPartitionedData();

        // create list of tasks to be run
        List<Callable<Vector>> workers = new ArrayList<>();

        for(int i = 0; i < size; i++){
            workers.add(new RankerWorker(subspaceRankers[i], partitionedData[i]));
        }

        try {
            // execute all tasks
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(size, Runtime.getRuntime().availableProcessors() - 1));
            List<Future<Vector>> scores = executor.invokeAll(workers);
            executor.shutdownNow();

            // compute final score
            Vector score = Vector.FACTORY.zeros(unlabeledData.length());

            for (Future<Vector> s : scores) {
                score.iAdd(s.get());
            }

            return score;

        } catch (InterruptedException ex) {
            throw new RuntimeException("Thread was abruptly interrupted.", ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException("Exception thrown at running task.", ex);
        }
    }

    /**
     * Helper class for multi-threaded score() method
     */
    private static class RankerWorker implements Callable<Vector> {

        private final Ranker ranker;
        private final IndexedDataset unlabeledData;

        RankerWorker(Ranker ranker, IndexedDataset unlabeledData) {
            this.ranker = ranker;
            this.unlabeledData = unlabeledData;
        }

        @Override
        public Vector call() {
            return ranker.score(unlabeledData);
        }
    }
}

