package data;

import utils.Validator;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A DataPoint is an indexed collection of values. More specifically, it is composed of two entities:
 *
 *   - id: a {@code long} uniquely identifying this data points (i.e. a database id).
 *   - data: array of {@code double} values representing the data point's content
 */
public class DataPoint {

    /**
     * data point's unique id
     */
    protected long id;

    /**
     * data point's values
     */
    protected double[] data;

    /**
     * @param id: data point's unique ID
     * @param data: the features array
     * @throws IllegalArgumentException if data is emtpy
     */
    public DataPoint(long id, double[] data) {
        Validator.assertNotEmpty(data);
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public double[] getData() {
        return data;
    }

    public double get(int i){
        return data[i];
    }

    /**
     * @param indices indices of selected attributes
     * @return map of the indices and the corresponding values
     */
    public HashMap<Integer, Double> getSelectedAttributesMap(int[] indices) {
        Arrays.sort(indices);

        HashMap<Integer, Double> selectedAttributes = new HashMap<>();
        for(int index: indices){
            selectedAttributes.put(index, data[index]);
        }
        return selectedAttributes;
    }

    /**
     * @param indices indices of selected attributes
     * @return map of the indices and the corresponding values
     */
    public double[] getSelectedAttributes(int[] indices) {
        Arrays.sort(indices);

        double[] selected = new double[indices.length];
        for(int index: indices){
            selected[index] = data[index];
        }
        return selected;
    }

    /**
     * @return data point's dimension (i.e. number of features)
     */
    public int getDim(){
        return data.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPoint)) return false;

        DataPoint dataPoint = (DataPoint) o;

        return id == dataPoint.id && Arrays.equals(data, dataPoint.data);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "{\"id\": " + getId()  + ", \"data\": " + Arrays.toString(getData()) + '}';
    }

    public DataPoint clone(double[] newData){
        return new DataPoint(id, newData);
    }
}
