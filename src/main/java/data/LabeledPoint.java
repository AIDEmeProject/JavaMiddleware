package data;

import machinelearning.classifier.Label;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private Label label;

    /**
     * @param row: row number of data point
     * @param id: data point's identifier
     * @param data: values array
     * @param label: data point's label
     * @throws IllegalArgumentException if data is empty
     * @throws IllegalArgumentException if label is different from 0 or 1
     */
    public LabeledPoint(int row, long id, double[] data, Label label) {
        super(row, id, data);
        this.label = label;
    }

    /**
     * @param row: row number of data point. It will also be used as id.
     * @param data: values array
     * @param label: data point's label
     * @throws IllegalArgumentException if data is empty
     * @throws IllegalArgumentException if label is different from 0 or 1
     */
    public LabeledPoint(int row, double[] data, Label label) {
        this(row, row, data, label);
    }

    /**
     * @param point: a data point
     * @param label: label
     * @throws IllegalArgumentException if label is different from 0 or 1
     */
    public LabeledPoint(DataPoint point, Label label) {
        this(point.row, point.id, point.data, label);
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
        return new LabeledPoint(row, id, dataWithBias, label);
    }

    /**
     * @param json JSON string
     * @return LabeledPoint object parsed from JSON string
     */
    public static LabeledPoint fromJson(String json){
        JSONObject jsonObject = new JSONObject(json);

        int row = jsonObject.getInt("row");
        int id = jsonObject.getInt("id");
        int label = jsonObject.getInt("label");

        JSONArray dataArray = jsonObject.getJSONArray("data");
        double[] data = new double[dataArray.length()];
        for (int i = 0; i < data.length; i++) {
            data[i] = dataArray.getDouble(i);
        }

        return new LabeledPoint(row, id, data, label == 1 ? Label.POSITIVE : Label.NEGATIVE);
    }

    /**
     * @return JSON encoding of this object
     */
    @Override
    public String toString() {
        return "{\"row\": " + row + ", \"id\": " + id  + ", \"data\": " + Arrays.toString(data) + ", \"label\": " + label.asBinary() + '}';
    }

    /**
     * @param newData: new data array
     * @return a new LabeledPoint with new data, but the same metadata
     */
    public LabeledPoint clone(double[] newData){
        return new LabeledPoint(row, id, newData, label);
    }
}
