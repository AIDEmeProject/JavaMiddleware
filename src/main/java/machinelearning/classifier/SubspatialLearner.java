package machinelearning.classifier;

import data.LabeledDataset;
import utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SubspatialLearner implements Learner {
    private final Learner[] subspaceLearners;

    public SubspatialLearner(Learner[] subspaceLearners) {
        Validator.assertNotEmpty(subspaceLearners);

        this.subspaceLearners = subspaceLearners;
    }

    @Override
    public SubspatialClassifier fit(LabeledDataset labeledPoints) {
        Validator.assertEquals(labeledPoints.partitionSize(), subspaceLearners.length);

        LabeledDataset[] partitionedData = labeledPoints.getPartitionedData();
        int size = partitionedData.length;

        // create list of tasks to be run
        List<Callable<Classifier>> workers = new ArrayList<>(size);

        for(int i = 0; i < size; i++){
            workers.add(new LearnerWorker(partitionedData[i], subspaceLearners[i]));
        }

        try {
            // execute all tasks
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(size, Runtime.getRuntime().availableProcessors() - 1));
            List<Future<Classifier>> subspaceClassifiersFuture = executor.invokeAll(workers);
            executor.shutdownNow();

            // parse result
            Classifier[] subspaceClassifiers = new Classifier[size];

            for (int i = 0; i < size; i++) {
                subspaceClassifiers[i] = subspaceClassifiersFuture.get(i).get();
            }

            return new SubspatialClassifier(labeledPoints.getPartitionIndexes(), subspaceClassifiers);
        } catch (InterruptedException ex) {
            throw new RuntimeException("Thread was abruptly interrupted.", ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException("Exception thrown at running task.", ex);
        }
    }

    /**
     * Helper class for multi-threaded fit() method
     */
    private static class LearnerWorker implements Callable<Classifier> {

        private final LabeledDataset labeledData;
        private final Learner learner;

        public LearnerWorker(LabeledDataset labeledData, Learner learner) {
            this.labeledData = labeledData;
            this.learner = learner;
        }

        @Override
        public Classifier call() {
            return learner.fit(labeledData);
        }
    }
}
