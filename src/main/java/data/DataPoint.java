package data;

import utils.linalg.Vector;

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
    protected Vector data;

    /**
     * @param id: data point's unique ID
     * @param data: the features array
     * @throws IllegalArgumentException if data is emtpy
     */
    public DataPoint(long id, double[] data) {
        this(id, Vector.FACTORY.make(data));
    }

    public DataPoint(long id, Vector data) {
        this.id = id;
        this.data = data;
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

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "{\"id\": " + getId()  + ", \"data\": " + data + '}';
    }

    public DataPoint clone(double[] newData){
        return new DataPoint(id, newData);
    }



    //Todo: move this part to Vector
    /**
     * @param indices indices of selected attributes
     * @return map of the indices and the corresponding values
     */
    public HashMap<Integer, Double> getSelectedAttributesMap(int[] indices) {
        Arrays.sort(indices);

        HashMap<Integer, Double> selectedAttributes = new HashMap<>();
        for(int index: indices){
            selectedAttributes.put(index, data.toArray()[index]);
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
        int selected_index = 0;
        for(int index: indices){
            selected[selected_index] = data.toArray()[index];
            selected_index++;
        }
        //System.out.println("The factorized vector: " + Arrays.toString(selected));
        return selected;
    }

}
