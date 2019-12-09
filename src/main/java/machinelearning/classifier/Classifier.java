package machinelearning.classifier;


import data.IndexedDataset;
import utils.linalg.Matrix;
import utils.linalg.Vector;

/**
 * A classifier is any object capable of "learning from training data" and "make predictions for new data points".
 *
 * @author luciano
 */
public interface Classifier {

    /**
     * @param vector: a feature vector
     * @return probability of vector belonging to the positive class
     */
    double probability(Vector vector);

    /**
     * @param matrix: a matrix whose every line corresponds to a feature vector
     * @return class probability estimation for each row of the matrix
     */
    default Vector probability(Matrix matrix) {
        double[] probas = new double[matrix.rows()];
        for (int i = 0; i < probas.length; i++) {
            probas[i] = probability(matrix.getRow(i));
        }
        return Vector.FACTORY.make(probas);
    }

    default Vector probability(IndexedDataset dataset) {
        return probability(dataset.getData());
    }

    /**
     * @param vector: a feature vector
     * @return predicted label for the input vector
     */
    default Label predict(Vector vector){
        return probability(vector) > 0.5 ? Label.POSITIVE : Label.NEGATIVE;
    }

    /**
     * @param matrix:  a matrix whose every line corresponds to a feature vector
     * @return predicted class labels for each row of the matrix
     */
    default Label[] predict(Matrix matrix) {
        Label[] labels = new Label[matrix.rows()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = predict(matrix.getRow(i));
        }
        return labels;
    }

    default Label[] predict(IndexedDataset dataset) {
        return predict(dataset.getData());
    }
}
