package machinelearning.classifier;

import utils.Validator;
import utils.linalg.Matrix;
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

    //TODO: implement this
//    @Override
//    public Vector probability(Matrix matrix) {
//        return null;
//    }

    @Override
    public Label predict(Vector vector) {
        for (int i=0; i < partitionIndexes.length; i++) {
            if(subspaceClassifiers[i].predict(vector.select(partitionIndexes[i])).isNegative()) {
                return Label.NEGATIVE;
            }
        }

        return Label.POSITIVE;
    }

    //TODO: implement this
//    @Override
//    public Label[] predict(Matrix matrix) {
//        return new Label[0];
//    }
}
