package data;

import java.util.Arrays;

/**
 * This class stores a single data points, together with its unique id.
 */
public class DataPoint {
    /**
     * data point's row number
     */
    private int id;

    /**
     * data point's values
     */
    private double[] data;

    public DataPoint(int id, double[] data) {
        if (data.length == 0){
            throw new IllegalArgumentException("Data point must be non-empty.");
        }
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public double[] getData() {
        return data;
    }

    public int getDim(){
        return data.length;
    }

    @Override
    public String toString() {
        return "(id=" + id + ", data=" + Arrays.toString(data) + ')';
    }
}
