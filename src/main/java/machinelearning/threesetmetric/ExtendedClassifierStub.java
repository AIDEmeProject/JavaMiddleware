package machinelearning.threesetmetric;

import data.DataPoint;
import machinelearning.classifier.UserLabel;
import utils.linalg.Vector;

import java.util.Collection;

/**
 * A stub for a {@link ExtendedClassifier}.
 */
public final class ExtendedClassifierStub implements ExtendedClassifier {
    /**
     * Nothing is done
     */
    @Override
    public void update(Vector point, UserLabel label) {
        // do nothing
    }

    /**
     * @return {@link ExtendedLabel#UNKNOWN}
     */
    @Override
    public ExtendedLabel predict(Vector point) {
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
