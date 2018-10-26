package data;

import utils.linalg.Vector;

import java.util.Arrays;

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
    private long id;

    /**
     * data point's values
     */
    private Vector data;

    /**
     * @param id: data point's unique ID
     * @param data: the features array
     * @throws IllegalArgumentException if data is emtpy
     */
    public DataPoint(long id, Vector data) {
        this.id = id;
        this.data = data;
    }

    public DataPoint(long id, double[] data) {
        this.id = id;
        this.data = Vector.FACTORY.make(data);
    }

    public long getId() {
        return id;
    }

    public Vector getData() {
        return data;
    }

    public double get(int i){
        return data.get(i);
    }

    /**
     * @return data point's dimension (i.e. number of features)
     */
    public int getDim(){
        return data.dim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPoint)) return false;

        DataPoint dataPoint = (DataPoint) o;
        return id == dataPoint.id && data.equals(dataPoint.data);
    }

    /**
     * @param indices indices of selected attributes
     * @return map of the indices and the corresponding values
     */
    public DataPoint getSelectedAttributes(int[] indices) {
        Arrays.sort(indices);
        return new DataPoint(id, data.select(indices));
    }

    @Override
    public String toString() {
        return "{\"id\": " + getId()  + ", \"data\": " + data + '}';
    }
}
