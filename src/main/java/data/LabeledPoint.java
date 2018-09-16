package data;

import machinelearning.classifier.Label;

import java.util.Arrays;
import java.util.Objects;

/**
 * A LabeledPoint is a {@link DataPoint} instance which was labeled by the user. More specifically, it is composed of three entities:
 *
 *   - id: a {@code long} uniquely identifying this data points (i.e. a database id).
 *   - data: array of {@code double} values representing the data point's content
 *   - label: the user {@link Label} (POSITIVE or NEGATIVE)
 */
public class LabeledPoint extends DataPoint {

    private Label label;

    /**
     * @param id: data point's identifier
     * @param data: values array
     * @param label: data point's label
     * @throws IllegalArgumentException if data is empty or label is {@code null}
     */
    public LabeledPoint(long id, double[] data, Label label) {
        super(id, data);
        this.label = Objects.requireNonNull(label);
    }

    /**
     * @param point: a data point
     * @param label: label
     * @throws IllegalArgumentException if label is {@code null}
     */
    public LabeledPoint(DataPoint point, Label label) {
        this(point.id, point.data, label);
    }

    public Label getLabel() {
        return label;
    }

    /**
     * @return a new Labeled Point with the value 1 appended to its left
     */
    public LabeledPoint addBias(){
        double[] dataWithBias = new double[getDim()+1];
        dataWithBias[0] = 1;
        System.arraycopy(data, 0, dataWithBias, 1, getDim());
        return new LabeledPoint(id, dataWithBias, label);
    }

    /**
     * @return JSON encoding of this object
     */
    @Override
    public String toString() {
        return "{\"id\": " + id  + ", \"data\": " + Arrays.toString(data) + ", \"label\": \"" + label + "\"}";
    }

    /**
     * @param newData: new data array
     * @return a new LabeledPoint with newData as data, but the same id and label
     */
    public LabeledPoint clone(double[] newData){
        return new LabeledPoint(id, newData, label);
    }
}
