package data;

import explore.user.UserLabel;
import machinelearning.classifier.Label;
import utils.linalg.Vector;

import java.util.Objects;

/**
 * A LabeledPoint is a {@link DataPoint} instance containing a {@link UserLabel}. More specifically, it is composed of three entities:
 *
 *   - id: a {@code long} uniquely identifying this data points (i.e. a database id).
 *   - data: array of {@code double} values representing the data point's content
 *   - label: a {@link UserLabel} (POSITIVE, NEGATIVE, ...)
 */
public class LabeledPoint {
    /**
     * Original data point
     */
    private DataPoint dataPoint;

    /**
     * User label
     */
    private UserLabel label;

    /**
     * @param point: a data point
     * @param label: user label
     * @throws NullPointerException if label is {@code null}
     */
    public LabeledPoint(DataPoint point, UserLabel label) {
        this.dataPoint = point;
        this.label = Objects.requireNonNull(label);
    }

    /**
     * @param id: data point's id
     * @param data: a data vector
     * @param label: user label
     * @throws NullPointerException if label is {@code null}
     */
    public LabeledPoint(long id, Vector data, UserLabel label) {
        this(new DataPoint(id, data), label);
    }

    public LabeledPoint(long id, double[] data, UserLabel label) {
        this(new DataPoint(id, data), label);
    }

    public long getId() {
        return dataPoint.getId();
    }

    public Vector getData() {
        return dataPoint.getData();
    }

    public UserLabel getLabel() {
        return label;
    }

    public int getDim() {
        return dataPoint.getDim();
    }

    public double get(int index) {
        return dataPoint.get(index);
    }

    /**
     * @param indices indices of selected attributes
     * @return map of the indices and the corresponding values
     */
    public LabeledPoint getSelectedAttributes(int[] indices, Label label) {
        return new LabeledPoint(dataPoint.getSelectedAttributes(indices), label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabeledPoint that = (LabeledPoint) o;
        return Objects.equals(dataPoint, that.dataPoint) &&
                Objects.equals(label, that.label);
    }

    /**
     * @return JSON encoding of this object
     */
    @Override
    public String toString() {
        return "{\"id\": " + dataPoint.getId()  + ", \"data\": " + dataPoint.getData() + ", \"label\": " + label + "}";
    }
}
