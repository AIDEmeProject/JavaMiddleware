package machinelearning.classifier;

import data.IndexedDataset;
import utils.Validator;
import utils.linalg.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SubspatialClassifier implements Classifier {
    private final int[][] partitionIndexes;
    private final Classifier[] subspaceClassifiers;

    public SubspatialClassifier(int[][] partitionIndexes, Classifier[] subspaceClassifiers) {
        Validator.assertNotEmpty(partitionIndexes);
        Validator.assertEqualLengths(partitionIndexes, subspaceClassifiers);

        this.partitionIndexes = partitionIndexes;
        this.subspaceClassifiers = subspaceClassifiers;
    }

//    @Override
//    public double probability(Vector vector) {
//        double probability = 1.0;
//
//        for (int i=0; i < partitionIndexes.length; i++) {
//            probability *= subspaceClassifiers[i].probability(vector.select(partitionIndexes[i]));
//        }
//
//        return probability;
//    }
//
//    @Override
//    public Vector probability(IndexedDataset dataset) {
//        Validator.assertEquals(partitionIndexes, dataset.getPartitionIndexes());
//
//        Vector probability = subspaceClassifiers[0].probability(dataset.getPartitionedData()[0]);
//        for (int i=1; i < partitionIndexes.length; i++) {
//            probability.iMultiply(subspaceClassifiers[i].probability(dataset.getPartitionedData()[i]));
//        }
//
//        return probability;
//    }

    @Override
    public double probability(Vector vector) {
        double minProbability = 1, probability;

        for (int i=0; i < partitionIndexes.length; i++) {
            probability = subspaceClassifiers[i].probability(vector.select(partitionIndexes[i]));
            if(probability < minProbability) {
                minProbability = probability;
            }
        }

        return minProbability;
    }

    @Override
    public Vector probability(IndexedDataset dataset) {
        Validator.assertEquals(dataset.getPartitionIndexes(), partitionIndexes);

        Vector[] subspaceProbabilities = probabilityAllSubspaces(dataset);
        Vector probability = subspaceProbabilities[0];

        for (int i = 1; i < subspaceProbabilities.length; i++) {
            for (int j = 0; j < probability.dim(); j++) {
                probability.set(j, Math.min(probability.get(j), subspaceProbabilities[i].get(j)));
            }
        }

        return probability;
    }

    public Vector[] probabilityAllSubspaces(IndexedDataset dataset) {
        int size = subspaceClassifiers.length;
        IndexedDataset[] partitionedData = dataset.getPartitionedData();

        // create list of tasks to be run
        List<Callable<Vector>> workers = new ArrayList<>();

        for(int i = 0; i < size; i++){
            workers.add(new ProbabilityWorker(subspaceClassifiers[i], partitionedData[i]));
        }

        try {
            // execute all tasks
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(size, Runtime.getRuntime().availableProcessors() - 1));
            List<Future<Vector>> scores = executor.invokeAll(workers);
            executor.shutdownNow();

            Vector[] probabilities = new Vector[size];

            for (int i=0; i < size; i++) {
                probabilities[i] = scores.get(i).get();
            }

            return probabilities;

        } catch (InterruptedException ex) {
            throw new RuntimeException("Thread was abruptly interrupted.", ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException("Exception thrown at running task.", ex);
        }
    }

    @Override
    public Label[] predict(IndexedDataset dataset) {
        Vector proba = probability(dataset);

        Label[] predictions = new Label[dataset.length()];
        for (int i = 0; i < dataset.length(); i++) {
            predictions[i] = proba.get(i) > 0.5 ? Label.POSITIVE : Label.NEGATIVE;
        }

        return predictions;
    }

    public Label[][] predictAllSubspaces(IndexedDataset dataset) {
        int size = dataset.partitionSize();
        IndexedDataset[] partitionedDatasets = dataset.getPartitionedData();

        Label[][] allLabels = new Label[size][dataset.length()];
        for (int i = 0; i < size; i++) {
            allLabels[i] = subspaceClassifiers[i].predict(partitionedDatasets[i]);
        }

        return allLabels;
    }

    /**
     * Helper class for multi-threaded score() method
     */
    private static class ProbabilityWorker implements Callable<Vector> {

        private final Classifier classifier;
        private final IndexedDataset unlabeledData;

        ProbabilityWorker(Classifier classifier, IndexedDataset unlabeledData) {
            this.classifier = classifier;
            this.unlabeledData = unlabeledData;
        }

        @Override
        public Vector call() {
            return classifier.probability(unlabeledData);
        }
    }
}
