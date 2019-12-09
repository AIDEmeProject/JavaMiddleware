package machinelearning.classifier;

import data.LabeledDataset;
import utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;


/**
 * Trains a learner object per subspace, returning a {@link SubspatialClassifier}. Training for each subspace is done
 * concurrently.
 */
public class SubspatialLearner implements Learner {
    /**
     * Learners to train on each subspace
     */
    private final Learner[] subspaceLearners;

    /**
     * Multi-threaded runner
     */
    private final SubspatialWorker worker;

    public SubspatialLearner(Learner[] subspaceLearners, SubspatialWorker worker) {
        Validator.assertNotEmpty(subspaceLearners);

        this.subspaceLearners = subspaceLearners;
        this.worker = Objects.requireNonNull(worker);
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

        return new SubspatialClassifier(
                labeledPoints.getPartitionIndexes(),
                worker.run(workers).toArray(new Classifier[0]),
                worker
        );
    }

    /**
     * Helper class for multi-threaded fit() method
     */
    private static class LearnerWorker implements Callable<Classifier> {

        private final LabeledDataset labeledData;
        private final Learner learner;

        LearnerWorker(LabeledDataset labeledData, Learner learner) {
            this.labeledData = labeledData;
            this.learner = learner;
        }

        @Override
        public Classifier call() {
            return learner.fit(labeledData);
        }
    }
}
