package data;

import utils.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This class stores a data point. It contains:
 *
 *   - row: row number in the original dataset
 *   - id: data point's id (i.e. database id). If not specified, the own row number is used
 *   - data: array of values contained in array
 */
public class DataPoint {
    /**
     * data point's row number
     */
    protected int row;

    /**
     * data point's id
     */
    protected long id;

    /**
     * data point's values
     */
    protected double[] data;

    /**
     *
     */

    /**
     * @param id: ID of this point
     * @param row: row number
     * @param data: the features array
     * @throws IllegalArgumentException if data is emtpy
     */
    public DataPoint(int row, long id, double[] data) {
        Validator.assertNotEmpty(data);

        this.row = row;
        this.id = id;
        this.data = data;
    }

    /**
     * Initializes data point with identical row and id values.
     * @param row: value to be used as row number and ID
     * @param data: the features array
     * @throws IllegalArgumentException if data is emtpy
     */
    public DataPoint(int row, double[] data) {
        this(row, row, data);
    }

    public int getRow() {
        return row;
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
    //Todo: retrieve data by id? In addition, this class lacks of feature selection

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

        return row == dataPoint.row && id == dataPoint.id && Arrays.equals(data, dataPoint.data);
    }

    /**
     * We return the own row number of the point, since it is (in theory) unique give a database.
     */
    @Override
    public int hashCode() {
        return row;
    }

    @Override
    public String toString() {
        return "{\"row\": " + getRow() + ", \"id\": " + getId()  + ", \"data\": " + Arrays.toString(getData()) + '}';
    }

    public DataPoint clone(double[] newData){
        return new DataPoint(row, id, newData);
    }
}
