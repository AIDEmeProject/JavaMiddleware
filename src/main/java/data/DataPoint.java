package data;

import utils.Validator;

import java.util.Arrays;

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

    public DataPoint(int row, long id, double[] data) {
        Validator.assertNotEmpty(data);

        this.row = row;
        this.id = id;
        this.data = data;
    }

    /**
     * Initializes data point with identical row and id values.
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
}
