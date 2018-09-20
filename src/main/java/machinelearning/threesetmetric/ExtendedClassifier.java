package machinelearning.threesetmetric;

import data.DataPoint;
import data.ExtendedLabel;
import machinelearning.classifier.Label;

import java.util.Collection;

public interface ExtendedClassifier {
    void update(double[] point, Label label);

    ExtendedLabel predict(double[] point);

    default ExtendedLabel[] predict(Collection<DataPoint> points) {
        return points.stream()
                .map(x -> predict(x.getData()))
                .toArray(ExtendedLabel[]::new);
    }
}
