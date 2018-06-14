package data;

/**
 * This class stores a single data points, together with its unique id.
 */
public class DataPoint {
    /**
     * data point's unique id
     */
    private long id;

    /**
     * data point's values
     */
    private double[] data;

    public DataPoint(long id, double[] data) {
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public double[] getData() {
        return data;
    }

    public int getDim(){
        return data.length;
    }
}
