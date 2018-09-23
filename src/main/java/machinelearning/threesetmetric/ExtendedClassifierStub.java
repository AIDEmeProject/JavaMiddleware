package machinelearning.threesetmetric;

import data.DataPoint;
import machinelearning.classifier.Label;

import java.util.Collection;

/**
 * A stub for a {@link ExtendedClassifier}.
 */
public final class ExtendedClassifierStub implements ExtendedClassifier {
    /**
     * Nothing is done
     */
    @Override
    public void update(double[] point, Label label) {
        // do nothing
    }

    /**
     * @return {@link ExtendedLabel#UNKNOWN}
     */
    @Override
    public ExtendedLabel predict(double[] point) {
        return ExtendedLabel.UNKNOWN;
    }

    /**
     * @return an empty array
     */
    @Override
    public ExtendedLabel[] predict(Collection<DataPoint> points) {
        return new ExtendedLabel[0];
    }
}
