package machinelearning.classifier.margin;

import data.IndexedDataset;
import utils.linalg.Matrix;
import utils.linalg.Vector;


public class FakeKernelClassifier extends KernelClassifier {

    public FakeKernelClassifier(KernelClassifier classifier) {
        super(classifier);
    }

    @Override
    public double probability(Vector vector) {
        return super.margin(vector);
    }

    @Override
    public Vector probability(Matrix matrix) {
        return super.margin(matrix);
    }

    @Override
    public Vector probability(IndexedDataset dataset) {
        return probability(dataset.getData());
    }
}
