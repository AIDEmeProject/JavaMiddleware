package machinelearning.threesetmetric;

import data.DataPoint;
import data.ExtendedLabel;
import machinelearning.classifier.Label;

import java.util.Collection;

public class ExtendedClassifierStub implements ExtendedClassifier {
    @Override
    public void update(double[] point, Label label) {
        // do nothing
    }

    @Override
    public ExtendedLabel predict(double[] point) {
        return ExtendedLabel.UNKNOWN;
    }

    @Override
    public ExtendedLabel[] predict(Collection<DataPoint> points) {
        return new ExtendedLabel[0];
    }
}
