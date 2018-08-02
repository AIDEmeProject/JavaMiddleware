package data;

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

        if (label < 0 || label > 1) {
            throw new IllegalArgumentException("Only 1 and 0 labels are supported, received " + label);
        }

        this.label = label;
    }

    /**
     * @param row: row number of data point. It will also be used as id.
     * @param data: values array
     * @param label: data point's label
     * @throws IllegalArgumentException if data is empty
     * @throws IllegalArgumentException if label is different from 0 or 1
     */
    public LabeledPoint(int row, double[] data, int label) {
        this(row, row, data, label);
    }

    public LabeledPoint(DataPoint point, int label) {
        this(point.row, point.id, point.data, label);
    }

    public int getLabel() {
        return label;
    }

    public static LabeledPoint fromJson(JSONObject jsonObject){
        int row = jsonObject.getInt("row");
        int id = jsonObject.getInt("id");
        int label = jsonObject.getInt("label");

        JSONArray dataArray = jsonObject.getJSONArray("data");
        double[] data = new double[dataArray.length()];
        for (int i = 0; i < data.length; i++) {
            data[i] = dataArray.getDouble(i);
        }

        return new LabeledPoint(row, id, data, label);
    }

    @Override
    public String toString() {
        return "{\"row\": " + row + ", \"id\": " + id  + ", \"data\": " + Arrays.toString(data) + ", \"label\": " + label + '}';
    }
}
