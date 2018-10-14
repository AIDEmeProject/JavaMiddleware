package data;

import explore.user.UserLabel;
import machinelearning.classifier.Label;

import java.util.Objects;

/**
 * A LabeledPoint is a {@link DataPoint} instance which was labeled by the user. More specifically, it is composed of three entities:
 *
 *   - id: a {@code long} uniquely identifying this data points (i.e. a database id).
 *   - data: array of {@code double} values representing the data point's content
 *   - label: the user {@link Label} (POSITIVE or NEGATIVE)
 */
public class LabeledPoint extends DataPoint {

    private UserLabel label;

    /**
     * @param point: a data point
     * @param label: label
     * @throws IllegalArgumentException if label is {@code null}
     */
    public LabeledPoint(DataPoint point, UserLabel label) {
        super(point.id, point.data);
        this.label = Objects.requireNonNull(label);
    }

    public UserLabel getLabel() {
        return label;
    }

    /**
     * @return JSON encoding of this object
     */
    @Override
    public String toString() {
        return "{\"id\": " + id  + ", \"data\": " + data + ", \"label\": " + label + "}";
    }
}
