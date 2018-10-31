package machinelearning.classifier;

import machinelearning.classifier.svm.Kernel;
import utils.linalg.Matrix;
import utils.linalg.Vector;


public class KernelMajorityVote implements Classifier {
    private Kernel kernel;
    private Matrix supportVectors;
    private Classifier linearMajorityVote;

    public KernelMajorityVote(Classifier linearMajorityVote, Matrix supportVectors, Kernel kernel) {
        this.linearMajorityVote = linearMajorityVote;
        this.supportVectors = supportVectors;
        this.kernel = kernel;
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
