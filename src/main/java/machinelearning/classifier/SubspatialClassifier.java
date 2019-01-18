package machinelearning.classifier;

import data.IndexedDataset;
import utils.Validator;
import utils.linalg.Vector;

public class SubspatialClassifier implements Classifier {
    private final int[][] partitionIndexes;
    private final Classifier[] subspaceClassifiers;

    public SubspatialClassifier(int[][] partitionIndexes, Classifier[] subspaceClassifiers) {
        Validator.assertNotEmpty(partitionIndexes);
        Validator.assertEqualLengths(partitionIndexes, subspaceClassifiers);

        this.partitionIndexes = partitionIndexes;
        this.subspaceClassifiers = subspaceClassifiers;
    }

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
        int size = dataset.partitionSize();
        IndexedDataset[] partitionedDatasets = dataset.getPartitionedData();
        Vector proba = subspaceClassifiers[0].probability(partitionedDatasets[0]);

        for (int i = 1; i < size; i++) {
            Vector newProba = subspaceClassifiers[i].probability(partitionedDatasets[i]);
            for (int j = 0; j < proba.dim(); j++) {
                proba.set(j, Math.min(proba.get(j), newProba.get(j)));
            }
        }

        return proba;
    }

    @Override
    public Label predict(Vector vector) {
        for (int i=0; i < partitionIndexes.length; i++) {
            if(subspaceClassifiers[i].predict(vector.select(partitionIndexes[i])).isNegative()) {
                return Label.NEGATIVE;
            }
        }

        return Label.POSITIVE;
    }

    @Override
    public Label[] predict(IndexedDataset dataset) {
        int size = dataset.partitionSize();
        IndexedDataset[] partitionedDatasets = dataset.getPartitionedData();
        Label[][] allLabels = new Label[size][dataset.length()];

        for (int i = 0; i < size; i++) {
            allLabels[i] = subspaceClassifiers[i].predict(partitionedDatasets[i]);
        }

        Label[] labels = new Label[dataset.length()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = Label.POSITIVE;

            for (int j = 0; j < size; j++) {
                if (allLabels[i][j].isNegative()){
                    labels[i] = Label.NEGATIVE;
                    break;
                }
            }
        }

        return labels;
    }
}
