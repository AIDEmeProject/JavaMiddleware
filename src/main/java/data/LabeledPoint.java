package data;

import java.util.Arrays;

/**
 * This class stores a labeled data point. It contains:
 *
 *   - row: row number in the original dataset
 *   - id: data point's id (i.e. database id). If not specified, the own row number is used
 *   - data: array of values contained in array
 *   - label: data point's label (0 or 1)
 */
public class LabeledPoint extends DataPoint {
    /**
     * data point's label
     */
    private int label;

    /**
     * @param row: row number of data point
     * @param id: data point's identifier
     * @param data: values array
     * @param label: data point's label
     * @throws IllegalArgumentException if data is empty
     * @throws IllegalArgumentException if label is different from 0 or 1
     */
    public LabeledPoint(int row, long id, double[] data, int label) {
        super(row, id, data);
        setLabel(label);
    }

    public LabeledPoint(int row, double[] data, int label) {
        super(row, data);
        setLabel(label);
    }

    public LabeledPoint(DataPoint point, int label) {
        this(point.getRow(), point.getId(), point.getData(), label);
    }

    public int getLabel() {
        return label;
    }

    private void setLabel(int label) {
        if (label < 0 || label > 1){
            throw new IllegalArgumentException();
        }
        this.label = label;
    }

    @Override
    public String toString() {
        return "(row=" + getRow() + ", id=" + getId()  + ", data=" + Arrays.toString(getData()) + ", label=" + label + ')';
    }
}
