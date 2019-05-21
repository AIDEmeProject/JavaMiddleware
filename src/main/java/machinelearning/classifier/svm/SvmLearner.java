package machinelearning.classifier.svm;

import data.LabeledDataset;
import machinelearning.classifier.Learner;
import machinelearning.classifier.margin.KernelClassifier;
import smile.classification.SVM;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.lang.reflect.Field;
import java.util.List;

/**
 * This module is responsible for training an SVM classifier over labeled data. Basically, it is a wrapper over Smile's
 * SVM implementation
 */
public class SvmLearner implements Learner {
    /**
     * SVM's penalty value
     */
    private final double C;

    /**
     * SVM kernel function
     */
    private final Kernel kernel;

    private static final int NUM_ITERS = 10;

    /**
     * @param C: penalty parameter
     * @param kernel: kernel function
     */
    public SvmLearner(double C, Kernel kernel) {
        Validator.assertPositive(C);
        Validator.assertNotNull(kernel);

        this.C = C;
        this.kernel = kernel;
    }

    public SvmLearner(SvmLearner learner) {
        this(learner.C, learner.kernel);
    }

    @Override
    public KernelClassifier fit(LabeledDataset labeledPoints) {
        return fit(labeledPoints, null);
    }

    /**
     * Trains a SVM classifier over the labeled data.
     * @param labeledPoints: labeled data
     * @return fitted SVM model as a KernelClassifier instance
     */
    @Override
    public KernelClassifier fit(LabeledDataset labeledPoints, Vector sampleWeights) {
        if (sampleWeights != null) {
            Validator.assertEquals(labeledPoints.length(), sampleWeights.dim());
        }

        SVM<double[]> svm = new SVM<>(kernel.getSmileKernel(labeledPoints.dim()), C);

        double[][] data = labeledPoints.getData().toArray();

        int[] labels = new int[labeledPoints.length()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = labeledPoints.get(i).getLabel().asBinary();
        }

        double[] weights = sampleWeights == null ? null : sampleWeights.toArray();

        for (int i = 0; i < NUM_ITERS; i++) {
            svm.learn(data, labels, weights);
        }
        svm.finish();

        List<SVM<double[]>.SupportVector> supportVectors = svm.getSupportVectors();

        // happens when all labels are identical. In this case, we set a single support vector with alpha = 0.
        if (supportVectors.isEmpty()) {
            supportVectors.add(svm.new SupportVector(new double[labeledPoints.dim()], 0, 0));
        }

        int size = supportVectors.size();

        Vector alpha = Vector.FACTORY.zeros(size);
        Matrix sv = Matrix.FACTORY.zeros(size, labeledPoints.dim());

        for (int i=0; i < size; i++) {
            SVM<double[]>.SupportVector supportVector = supportVectors.get(i);
            alpha.set(i, supportVector.alpha);
            sv.setRow(i, supportVector.x);
        }

        return new KernelClassifier(getBias(svm), alpha, sv, kernel);
    }

    private double getBias(SVM<double[]> svm) {
        double bias;
        try {
            Field f = svm.getClass().getDeclaredField("svm");
            f.setAccessible(true);
            Field f2 = f.get(svm).getClass().getDeclaredField("b");
            f2.setAccessible(true);
            bias = (double) f2.get(f.get(svm));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return bias;
    }
}
