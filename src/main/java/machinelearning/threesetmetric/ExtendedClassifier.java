package machinelearning.threesetmetric;

import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Label;

import java.util.Collection;

/**
 * A ExtendedClassifier is responsible for building an accurate model of the user interest and disinterest regions. This model
 * can be continuously updated as the real user provides more feedback, and for any given point its label can be predicted.
 *
 * In contrast to the usual Machine Learning classifier, a ExtendedClassifier may return one of three possible labels:
 * POSITIVE, NEGATIVE, or UNKNOWN. See {@link ExtendedLabel} for more details.
 */
public interface ExtendedClassifier {
//    /**
//     * Update the current data model with new labeled data.
//     * @param point: a data array
//     * @param label: the data's label
//     */
//    void update(double[] point, Label label);

    /**
     * Update the current data model with new labeled data.
     * @param labeledPoints a {@link LabeledPoint} instance
     */
    void update(Collection<LabeledPoint> labeledPoints);
//    /**
//     * @param point: a data point
//     * @return the predicted label for input point
//     */
//    ExtendedLabel predict(double[] point);

    /**
     * @param dataPoint: a data point
     * @return the predicted label for input point
     */
    ExtendedLabel predict(DataPoint dataPoint);

    /**
     * @param points: a collection of data point
     * @return the predicted labels for each point in the input collection
     */
    default ExtendedLabel[] predict(Collection<DataPoint> points) {
        return points.stream()
                .map(this::predict)
                .toArray(ExtendedLabel[]::new);
    }
}
