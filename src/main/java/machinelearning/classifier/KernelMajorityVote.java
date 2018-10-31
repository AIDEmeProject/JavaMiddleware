package machinelearning.classifier;

import machinelearning.classifier.margin.LinearClassifier;
import machinelearning.classifier.svm.Kernel;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

public class KernelMajorityVote extends MajorityVote<LinearClassifier> {
    private Kernel kernel;
    private Matrix supportVectors;
    private LinearMajorityVote linearMajorityVote;

    public KernelMajorityVote(LinearClassifier[] linearClassifiers, Matrix supportVectors, Kernel kernel) {
        super(linearClassifiers);

        this.kernel = kernel;
        this.supportVectors = supportVectors;
        this.linearMajorityVote = new LinearMajorityVote(linearClassifiers);
        Validator.assertEquals(linearMajorityVote.getDim(), supportVectors.rows());
    }

    @Override
    public double probability(Vector vector) {
        return linearMajorityVote.probability(margin(vector));
    }

    @Override
    public Vector probability(Matrix matrix) {
        return linearMajorityVote.probability(margin(matrix));
    }

    @Override
    public Label predict(Vector vector) {
        return linearMajorityVote.predict(margin(vector));
    }

    @Override
    public Label[] predict(Matrix matrix) {
        return linearMajorityVote.predict(margin(matrix));
    }

    private Vector margin(Vector vector) {
        return kernel.compute(supportVectors, vector);
    }

    private Matrix margin(Matrix matrix) {
        return kernel.compute(matrix, supportVectors);
    }
}
